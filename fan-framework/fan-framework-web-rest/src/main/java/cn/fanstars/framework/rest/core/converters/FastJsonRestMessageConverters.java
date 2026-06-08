package cn.fanstars.framework.rest.core.converters;

import cn.fanstars.framework.common.util.spring.SpringUtils;
import cn.fanstars.framework.rest.config.RestClientProperties;
import cn.fanstars.framework.rest.core.dto.RestRawJsonDTO;
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
 * 会清除 {@code HttpServiceFactory} 默认的 Jackson 转换器，避免与 Fastjson 注解冲突。
 * <p>
 * 入口方法选用：
 * <ul>
 *   <li>{@link #apply} — 常规 JSON API</li>
 *   <li>{@link #applyWithTextBody} — 响应 Content-Type 可能为 {@code text/html}/{@code text/plain}，body 实为 JSON</li>
 *   <li>{@link #applyWithTextBodyAndRawResponse} — 同上，且向 {@link RestRawJsonDTO} 注入 {@code rawJson}（供验签等）</li>
 * </ul>
 * <p>
 * 关于注册多个 {@link HttpMessageConverter}：Spring 按列表顺序选用第一个 {@code canRead} 为 true 的转换器，
 * 因此同一 {@code MediaType} 不应注册两个 Fastjson 转换器；compare / rawJson 通过
 * {@link BufferingFastJsonHttpMessageConverter} 的 {@code (compare, captureRaw)} 两个开关组合。
 * <p>
 * 用法示例：
 * <pre>{@code
 * httpServiceFactory.getRestClient(baseUrl)
 *     .mutate()
 *     .messageConverters(FastJsonRestMessageConverters::applyWithTextBodyAndRawResponse)
 *     .build();
 * }</pre>
 *
 * @author aquan
 */
public final class FastJsonRestMessageConverters {

    /**
     * 工具类，禁止实例化
     */
    private FastJsonRestMessageConverters() {
    }

    /**
     * 重置并注册：表单 + UTF-8 文本 + Fastjson JSON
     *
     * @param converters RestClient 的 {@link HttpMessageConverter} 列表（会被 {@code clear} 后重建）
     */
    public static void apply(List<HttpMessageConverter<?>> converters) {
        apply(converters, true);
    }

    /**
     * 在 {@link #apply} 基础上，额外兼容 {@code text/html}、{@code text/plain} 响应体（内容为 JSON 的场景）
     *
     * @param converters RestClient 的 {@link HttpMessageConverter} 列表
     */
    public static void applyWithTextBody(List<HttpMessageConverter<?>> converters) {
        apply(converters, true);
        extendTextBodyMediaTypes(converters);
    }

    /**
     * 重置并注册 Fastjson JSON；可选是否同时注册表单与纯文本转换器
     *
     * @param converters           RestClient 的 {@link HttpMessageConverter} 列表
     * @param includeFormAndString 是否注册 {@link FormHttpMessageConverter} 与 {@link StringHttpMessageConverter}
     */
    public static void apply(List<HttpMessageConverter<?>> converters, boolean includeFormAndString) {
        registerConverters(converters, includeFormAndString, shouldUseComparingConverter(), false);
    }

    /**
     * 在 {@link #applyWithTextBody} 基础上，缓存原始响应 JSON 并注入 {@link RestRawJsonDTO#getRawJson()}。
     * <p>
     * 通过 {@link #createFastJsonConverter(boolean, boolean)} 创建 {@code Buffering(compare, captureRaw=true)}，
     * 生产环境也走 Buffering（仅开启 captureRaw）；非生产且 compare 开关打开时可叠加比对。
     * {@code rawJson} 不参与 JSON 序列化，见 {@link RestRawJsonDTO}。
     *
     * @param converters RestClient 的 {@link HttpMessageConverter} 列表
     */
    public static void applyWithTextBodyAndRawResponse(List<HttpMessageConverter<?>> converters) {
        registerConverters(converters, true, shouldUseComparingConverter(), true);
        extendTextBodyMediaTypes(converters);
    }

    /**
     * 重置转换器列表并注册表单、文本、Fastjson 转换器
     *
     * @param converters           RestClient 的 {@link HttpMessageConverter} 列表
     * @param includeFormAndString 是否注册表单与纯文本转换器
     * @param compare              是否启用开发期 compare，见 {@link #shouldUseComparingConverter()}
     * @param captureRaw           是否注入 {@link RestRawJsonDTO#getRawJson()}
     */
    private static void registerConverters(List<HttpMessageConverter<?>> converters, boolean includeFormAndString,
                                           boolean compare, boolean captureRaw) {
        converters.clear(); // 去掉 HttpServiceFactory 默认的 Jackson，避免与 @JSONField 冲突
        if (includeFormAndString) {
            converters.add(new FormHttpMessageConverter()); // application/x-www-form-urlencoded
            converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        }
        converters.add(createFastJsonConverter(compare, captureRaw));
    }

    /**
     * 扩展 Fastjson 转换器支持的 MediaType，兼容响应体为 JSON 但 Content-Type 标为纯文本的场景。
     * <p>
     * 部分 HTTP 服务返回 JSON 数据，却将 {@code Content-Type} 设为 {@code text/html} 或
     * {@code text/plain}。Spring 默认只认 {@code application/json}，会抛出
     * {@code UnknownContentTypeException}。本方法在已有 Fastjson 转换器上追加这两种类型，
     * 使 RestClient 仍能正常反序列化响应。
     *
     * @param converters {@link #apply} 注册后的转换器列表
     */
    private static void extendTextBodyMediaTypes(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof FastJsonHttpMessageConverter fastJson) {
                List<MediaType> mediaTypes = new ArrayList<>(fastJson.getSupportedMediaTypes());
                mediaTypes.add(MediaType.TEXT_HTML);  // body 为 JSON，Content-Type 误标为 text/html
                mediaTypes.add(MediaType.TEXT_PLAIN); // body 为 JSON，Content-Type 误标为 text/plain
                fastJson.setSupportedMediaTypes(mediaTypes);
                return;
            }
        }
    }

    /**
     * 创建 Fastjson 转换器
     * <p>
     * {@code compare} 或 {@code captureRaw} 为 true 时使用 {@link BufferingFastJsonHttpMessageConverter}；
     * 否则使用原生 {@link FastJsonHttpMessageConverter}，避免无谓 body 缓存。
     * <ul>
     *   <li>{@code (false, false)} — 原生 Fastjson</li>
     *   <li>{@code (true, false)} — 开发期 compare</li>
     *   <li>{@code (false, true)} — 生产 rawJson 注入</li>
     *   <li>{@code (true, true)} — compare + rawJson</li>
     * </ul>
     *
     * @param compare    是否比对「原始 JSON vs 反序列化结果」
     * @param captureRaw 是否向 {@link RestRawJsonDTO} 注入 {@code rawJson}
     * @return 已配置 {@code application/json} 与 {@code application/*+json} 的转换器
     */
    public static FastJsonHttpMessageConverter createFastJsonConverter(boolean compare, boolean captureRaw) {
        FastJsonHttpMessageConverter fastJson = (compare || captureRaw)
                ? new BufferingFastJsonHttpMessageConverter(compare, captureRaw)
                : new FastJsonHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(new MediaType("application", "*+json")); // 兼容 application/vnd.api+json 等
        fastJson.setSupportedMediaTypes(mediaTypes);
        return fastJson;
    }

    /**
     * 创建默认 Fastjson 转换器
     * <p>
     * 等价于 {@code createFastJsonConverter(shouldUseComparingConverter(), false)}：
     * compare 随环境，不捕获 rawJson。
     *
     * @return 默认配置的 Fastjson 转换器
     */
    public static FastJsonHttpMessageConverter createFastJsonConverter() {
        return createFastJsonConverter(shouldUseComparingConverter(), false);
    }

    /**
     * 是否启用「原始 JSON vs 反序列化结果」比对
     * <p>
     * 仅非生产环境生效，受 {@code fan.rest.log.compare-enabled} 控制。
     *
     * @return {@code true} 表示应使用带 compare 能力的 {@link BufferingFastJsonHttpMessageConverter}
     */
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
     * 将响应字节按响应头声明的字符集解码为文本；若未声明则回退 UTF-8
     *
     * @param responseBody 响应原始字节
     * @param headers      响应头
     * @return 解码后的响应文本
     */
    private static String toResponseBodyText(byte[] responseBody, HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        // 优先使用响应头 charset，缺失时回退 UTF-8，避免 compare / rawJson 解码偏差
        Charset charset = contentType != null && contentType.getCharset() != null
                ? contentType.getCharset()
                : StandardCharsets.UTF_8;
        return new String(responseBody, charset);
    }

    /**
     * 先缓存响应 body 再反序列化，可选开发期 compare 与 {@link RestRawJsonDTO#getRawJson()} 注入。
     * <p>
     * 处理顺序：缓存字节 → Fastjson 反序列化 →（可选）compare →（可选）setRawJson。
     * compare 在注入 {@code rawJson} 之前执行，避免比对结果受该字段干扰；
     * {@code rawJson} 带 {@code serialize=false}，不会进入 compare 的序列化结果。
     */
    static final class BufferingFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {

        /**
         * 是否调用 {@link FastjsonResponseCompareUtils} 做开发期比对
         */
        private final boolean compare;
        /**
         * 是否将原始响应文本写入 {@link RestRawJsonDTO#getRawJson()}
         */
        private final boolean captureRaw;

        /**
         * @param compare    是否启用开发期 compare
         * @param captureRaw 是否注入 {@link RestRawJsonDTO#getRawJson()}
         */
        BufferingFastJsonHttpMessageConverter(boolean compare, boolean captureRaw) {
            this.compare = compare;
            this.captureRaw = captureRaw;
        }

        /**
         * 按完整 Type 进行响应反序列化（覆盖泛型返回值，如 {@code List<Foo>}）
         *
         * @param type         目标类型（含泛型）
         * @param contextClass 上下文 Class
         * @param inputMessage 原始 HTTP 响应
         * @return 反序列化后的对象（可能已注入 {@code rawJson}）
         */
        @Override
        @NonNull
        public Object read(@NonNull Type type, @NonNull Class<?> contextClass, @NonNull HttpInputMessage inputMessage)
                throws IOException, HttpMessageNotReadableException {
            // 先缓存 body：后续既要交给 Fastjson 反序列化，也要用于 compare / rawJson
            byte[] responseBody = StreamUtils.copyToByteArray(inputMessage.getBody());
            CachedBodyHttpInputMessage cachedInputMessage = new CachedBodyHttpInputMessage(responseBody, inputMessage.getHeaders());
            // super.read(...) 会按目标 Type 真正反序列化，这也是比对能发现类型差异的关键
            Object converterResponse = super.read(type, contextClass, cachedInputMessage);
            return finishRead(converterResponse, responseBody, inputMessage.getHeaders());
        }

        /**
         * 按 Class 进行响应反序列化（无泛型 Type 信息的读取入口）
         *
         * @param clazz        目标 Class
         * @param inputMessage 原始 HTTP 响应
         * @return 反序列化后的对象（可能已注入 {@code rawJson}）
         */
        @Override
        @NonNull
        protected Object readInternal(@NonNull Class<?> clazz, @NonNull HttpInputMessage inputMessage)
                throws IOException, HttpMessageNotReadableException {
            // 兼容仅传 Class 的读取路径（无泛型 Type 场景）
            byte[] responseBody = StreamUtils.copyToByteArray(inputMessage.getBody());
            CachedBodyHttpInputMessage cachedInputMessage = new CachedBodyHttpInputMessage(responseBody, inputMessage.getHeaders());
            Object converterResponse = super.readInternal(clazz, cachedInputMessage);
            return finishRead(converterResponse, responseBody, inputMessage.getHeaders());
        }

        /**
         * 反序列化后的收尾：开发期比对、原始 JSON 注入（仅 {@link RestRawJsonDTO} 子类）
         *
         * @param converterResponse Fastjson 反序列化结果
         * @param responseBody      缓存的响应字节
         * @param headers           响应头（用于解析 charset）
         * @return {@code converterResponse} 本身
         */
        private Object finishRead(Object converterResponse, byte[] responseBody, HttpHeaders headers) {
            String responseBodyText = toResponseBodyText(responseBody, headers);
            if (compare) {
                FastjsonResponseCompareUtils.compareIfNeeded(responseBodyText, converterResponse);
            }
            if (captureRaw && converterResponse instanceof RestRawJsonDTO rawJsonResponse) {
                rawJsonResponse.setRawJson(responseBodyText);
            }
            return converterResponse;
        }

    }

    /**
     * 可重复读取的 {@link HttpInputMessage}，供缓存 body 后交给 Fastjson 解析
     *
     * @param body    缓存的响应体字节
     * @param headers 原始响应头
     */
    private record CachedBodyHttpInputMessage(byte[] body, HttpHeaders headers) implements HttpInputMessage {

        /**
         * {@inheritDoc}
         * <p>
         * 每次返回新的输入流，确保下游读取不会互相影响。
         */
        @Override
        @NonNull
        public InputStream getBody() {
            // 每次返回新的输入流，确保下游读取不会互相影响
            return new ByteArrayInputStream(body);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public HttpHeaders getHeaders() {
            return headers;
        }

    }

}
