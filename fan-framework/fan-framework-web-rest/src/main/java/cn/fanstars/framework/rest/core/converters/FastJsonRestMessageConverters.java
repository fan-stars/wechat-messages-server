package cn.fanstars.framework.rest.core.converters;

import cn.fanstars.framework.common.util.spring.SpringUtils;
import cn.fanstars.framework.rest.config.RestClientProperties;
import cn.fanstars.framework.rest.core.util.FastjsonResponseCompareUtils;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * RestClient 通用 Fastjson 消息转换器配置
 * <p>
 * 适用于依赖 {@code @JSONField}、Fastjson 自定义 Serializer/Deserializer 的 HTTP 客户端模块。
 * 用法示例：
 * <pre>{@code
 * httpServiceFactory.getRestClient(baseUrl)
 *     .mutate()
 *     .messageConverters(FastJsonRestMessageConverters::apply)
 *     .build();
 * }</pre>
 */
public final class FastJsonRestMessageConverters {

    private FastJsonRestMessageConverters() {
    }

    /**
     * 重置并注册：表单 + UTF-8 文本 + Fastjson JSON
     */
    public static void apply(List<HttpMessageConverter<?>> converters) {
        apply(converters, true);
    }

    /**
     * 重置并注册 Fastjson JSON；可选是否同时注册表单与纯文本转换器
     */
    public static void apply(List<HttpMessageConverter<?>> converters, boolean includeFormAndString) {
        converters.clear(); // 去掉 HttpServiceFactory 默认的 Jackson，避免与 @JSONField 冲突
        if (includeFormAndString) {
            converters.add(new FormHttpMessageConverter()); // application/x-www-form-urlencoded
            converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        }
        converters.add(createFastJsonConverter());
    }

    /**
     * 创建仅处理 JSON 的 Fastjson 转换器
     */
    public static FastJsonHttpMessageConverter createFastJsonConverter() {
        // 仅在「非生产 + compareEnabled=true」时启用比对版 converter，其他场景走原生实现降低开销
        FastJsonHttpMessageConverter fastJson = shouldUseComparingConverter()
                ? new ComparingFastJsonHttpMessageConverter()
                : new FastJsonHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(new MediaType("application", "*+json")); // 兼容 application/vnd.api+json 等
        fastJson.setSupportedMediaTypes(mediaTypes);
        return fastJson;
    }

    private static boolean shouldUseComparingConverter() {
        if (SpringUtils.isProd()) {
            // 生产环境禁用比对，避免额外 CPU/内存消耗
            return false;
        }
        try {
            RestClientProperties properties = SpringUtils.getBean(RestClientProperties.class);
            // 复用 fan.rest.log.compare-enabled 开关，统一控制比对行为
            return properties != null && properties.getLog() != null
                    && Boolean.TRUE.equals(properties.getLog().getCompareEnabled());
        } catch (Exception ignored) {
            // 非 Spring 上下文或未注册配置时，默认沿用非生产环境比对行为
            return true;
        }
    }

    /**
     * 在 Fastjson 实际反序列化后执行比对，逻辑与 retrofit2 FastjsonConverterFactory 对齐
     */
    private static final class ComparingFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

        /**
         * 按完整 Type 进行响应反序列化，并在反序列化后执行一次响应对比。
         * <p>
         * 该路径可覆盖泛型返回值（如 List&lt;Foo&gt;）场景，行为与 retrofit2 的 Type 解析路径保持一致。
         */
        @Override
        @NonNull
        public Object read(@NonNull Type type, @NonNull Class<?> contextClass, @NonNull HttpInputMessage inputMessage)
                throws IOException, HttpMessageNotReadableException {
            // 先缓存 body：后续既要交给 Fastjson 反序列化，也要用于原文对比
            byte[] responseBody = StreamUtils.copyToByteArray(inputMessage.getBody());
            CachedBodyHttpInputMessage cachedInputMessage = new CachedBodyHttpInputMessage(responseBody, inputMessage.getHeaders());
            // super.read(...) 会按目标 Type 真正反序列化，这也是比对能发现类型差异的关键
            Object converterResponse = super.read(type, contextClass, cachedInputMessage);
            // 使用「原始响应文本 vs 反序列化后对象」执行比对，行为对齐 retrofit2
            FastjsonResponseCompareUtils.compareIfNeeded(toResponseBodyText(responseBody, inputMessage.getHeaders()), converterResponse);
            return converterResponse;
        }

        /**
         * 按 Class 进行响应反序列化，并在反序列化后执行一次响应对比。
         * <p>
         * 用于没有泛型 Type 信息的读取入口，避免这条路径遗漏比对逻辑。
         */
        @Override
        @NonNull
        protected Object readInternal(@NonNull Class<?> clazz, @NonNull HttpInputMessage inputMessage)
                throws IOException, HttpMessageNotReadableException {
            // 兼容仅传 Class 的读取路径（无泛型 Type 场景）
            byte[] responseBody = StreamUtils.copyToByteArray(inputMessage.getBody());
            CachedBodyHttpInputMessage cachedInputMessage = new CachedBodyHttpInputMessage(responseBody, inputMessage.getHeaders());
            Object converterResponse = super.readInternal(clazz, cachedInputMessage);
            FastjsonResponseCompareUtils.compareIfNeeded(toResponseBodyText(responseBody, inputMessage.getHeaders()), converterResponse);
            return converterResponse;
        }

        /**
         * 将响应字节按响应头声明的字符集解码为文本；若未声明则回退 UTF-8。
         */
        private static String toResponseBodyText(byte[] responseBody, HttpHeaders headers) {
            MediaType contentType = headers.getContentType();
            // 优先使用响应头 charset，缺失时回退 UTF-8，避免比对前文本解码偏差
            Charset charset = contentType != null && contentType.getCharset() != null
                    ? contentType.getCharset()
                    : StandardCharsets.UTF_8;
            return new String(responseBody, charset);
        }

    }

    private record CachedBodyHttpInputMessage(byte[] body, HttpHeaders headers) implements HttpInputMessage {

        @Override
        @NonNull
        public InputStream getBody() {
            // 每次返回新的输入流，确保下游读取不会互相影响
            return new ByteArrayInputStream(body);
        }

        @Override
        @NonNull
        public HttpHeaders getHeaders() {
            return headers;
        }

    }

}
