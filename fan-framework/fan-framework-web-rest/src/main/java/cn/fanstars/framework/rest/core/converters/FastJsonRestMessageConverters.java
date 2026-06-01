package cn.fanstars.framework.rest.core.converters;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

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
        FastJsonHttpMessageConverter fastJson = new FastJsonHttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        mediaTypes.add(new MediaType("application", "*+json")); // 兼容 application/vnd.api+json 等
        fastJson.setSupportedMediaTypes(mediaTypes);
        return fastJson;
    }

}
