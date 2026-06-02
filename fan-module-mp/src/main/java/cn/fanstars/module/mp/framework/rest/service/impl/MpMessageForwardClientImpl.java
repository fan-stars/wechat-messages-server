package cn.fanstars.module.mp.framework.rest.service.impl;

import cn.fanstars.module.mp.framework.rest.bo.MpMessageForwardHttpResultBO;
import cn.fanstars.module.mp.framework.rest.service.MpMessageForwardClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.SocketTimeoutException;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 公众号消息透传转发 HTTP 客户端实现
 * <p>
 * 每条规则可配置不同 {@code timeoutMs}，因此按超时时间动态创建 JDK HttpClient / RestClient 实例。
 */
@Slf4j
@AllArgsConstructor
public class MpMessageForwardClientImpl implements MpMessageForwardClient {

    /**
     * 与微信回调一致的 XML Content-Type
     */
    private static final MediaType MEDIA_TYPE_XML = MediaType.parseMediaType("application/xml; charset=UTF-8");

    private final Executor fanRestExecutor;
    /**
     * 与 {@link cn.fanstars.framework.rest.core.impl.HttpServiceFactoryImpl} 一致，挂框架日志等拦截器
     */
    private final List<ClientHttpRequestInterceptor> interceptors;

    /**
     * {@inheritDoc}
     * <p>
     * 同步阻塞调用，占用 {@code mp-message-handle} 线程直至响应或超时。
     */
    @Override
    public MpMessageForwardHttpResultBO forward(String url, Map<String, String> headers,
                                                String rawContent, int timeoutMs) {
        RestClient restClient = createRestClient(timeoutMs);
        long start = System.currentTimeMillis();
        log.info("[forward][url({}) timeoutMs={} 开始透传]", url, timeoutMs);
        try {
            // 同步阻塞，占用 mp-message-handle 线程；uri 为绝对地址
            ResponseEntity<String> response = restClient.post()
                    .uri(url)
                    .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                    .contentType(MEDIA_TYPE_XML)
                    .body(rawContent)
                    .retrieve()
                    .toEntity(String.class);
            int durationMs = (int) (System.currentTimeMillis() - start);
            String responseBody = response.getBody();
            int httpStatus = response.getStatusCode().value();
            log.info("[forward][url({}) httpStatus={} durationMs={}]", url, httpStatus, durationMs);
            return MpMessageForwardHttpResultBO.success(responseBody, httpStatus, durationMs);
        } catch (Exception ex) {
            int durationMs = (int) (System.currentTimeMillis() - start);
            log.warn("[forward][url({}) 转发失败]", url, ex);
            // 超时等由上层记 TIMEOUT / FAILURE
            return MpMessageForwardHttpResultBO.failure(resolveErrorMsg(ex), durationMs);
        }
    }

    /**
     * 按规则超时创建 RestClient，复用 fan.rest 线程池与全局拦截器
     */
    private RestClient createRestClient(int timeoutMs) {
        Duration timeout = Duration.ofMillis(timeoutMs);
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .executor(fanRestExecutor)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);
        RestClient.Builder builder = RestClient.builder().requestFactory(requestFactory);
        if (interceptors != null && !interceptors.isEmpty()) {
            interceptors.forEach(builder::requestInterceptor);
        }
        return builder.build();
    }

    /**
     * 判断异常链中是否包含读/写/连接超时
     */
    private static boolean isTimeoutException(Throwable ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof SocketTimeoutException || cause instanceof HttpTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    /**
     * 统一错误文案，便于上层识别超时并写入 TIMEOUT 日志状态
     */
    private static String resolveErrorMsg(Exception ex) {
        if (isTimeoutException(ex)) {
            return "timeout";
        }
        return ex.getMessage();
    }

}
