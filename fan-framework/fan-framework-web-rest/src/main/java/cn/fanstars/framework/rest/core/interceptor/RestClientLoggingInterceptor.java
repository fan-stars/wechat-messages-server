package cn.fanstars.framework.rest.core.interceptor;

import cn.fanstars.framework.rest.config.RestClientProperties;
import cn.fanstars.framework.rest.core.client.BufferingClientHttpResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * RestClient 全局日志拦截器：记录请求/响应
 * <p>
 * 由 {@link cn.fanstars.framework.rest.config.FanRestAutoConfiguration} 注册为 Bean，
 * 随 {@link cn.fanstars.framework.rest.core.impl.HttpServiceFactoryImpl} 挂到所有 RestClient。
 * 开关见 {@code fan.rest.log.enabled}。
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) // 优先于其它拦截器，保证完整记录请求链路
public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final RestClientProperties.Log logProperties;

    public RestClientLoggingInterceptor(RestClientProperties properties) {
        this.logProperties = properties.getLog();
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body,
                                        @NonNull ClientHttpRequestExecution execution) throws IOException {
        long start = System.currentTimeMillis();
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        BufferingClientHttpResponseWrapper bufferingResponse = BufferingClientHttpResponseWrapper.buffer(response);
        logResponse(request, bufferingResponse, bufferingResponse.getBodyBytes(), System.currentTimeMillis() - start);
        return bufferingResponse;
    }

    /**
     * 打印出站请求：方法、URL、请求体
     */
    private void logRequest(HttpRequest request, byte[] body) {
        log.info("[RestClient] --> {} {}", request.getMethod(), request.getURI());
        if (body != null && body.length > 0) {
            log.info("[RestClient] --> body: {}", truncate(new String(body, StandardCharsets.UTF_8)));
        }
    }

    /**
     * 打印入站响应：状态码、耗时、响应体
     */
    private void logResponse(HttpRequest request, ClientHttpResponse response, byte[] responseBody, long costMs)
            throws IOException {
        String responseText = responseBody.length > 0 ? new String(responseBody, StandardCharsets.UTF_8) : "";
        log.info("[RestClient] <-- {} {} {} ({}ms)", request.getMethod(), request.getURI(),
                response.getStatusCode(), costMs);
        if (StringUtils.hasText(responseText)) {
            log.info("[RestClient] <-- body: {}", truncate(responseText));
        }
    }

    /**
     * 避免超大 body 刷屏日志
     */
    private String truncate(String text) {
        int maxLength = logProperties.getMaxBodyLength() != null ? logProperties.getMaxBodyLength() : 2048;
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...(truncated)";
    }

}
