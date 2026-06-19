package cn.fanstars.module.system.framework.sms.rest.support;

import cn.fanstars.framework.rest.core.HttpServiceFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * 短信厂商 web-rest 客户端构建辅助类
 * <p>
 * 基于 {@link HttpServiceFactory} 复用全局超时与日志拦截器，再叠加厂商鉴权拦截器并生成 {@code @HttpExchange} 代理。
 */
public final class SmsRestClientSupport {

    private SmsRestClientSupport() {
    }

    /**
     * 创建带厂商鉴权的 {@code @HttpExchange} 接口代理
     *
     * @param httpServiceFactory web-rest 工厂
     * @param baseUrl 厂商 baseUrl，须以 {@code /} 结尾
     * @param authInterceptor 厂商签名/鉴权拦截器
     * @param apiType {@code @HttpExchange} 接口类型
     * @return 声明式 HTTP 客户端代理
     */
    public static <T> T createClient(HttpServiceFactory httpServiceFactory, String baseUrl,
                                     ClientHttpRequestInterceptor authInterceptor, Class<T> apiType) {
        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        RestClient restClient = httpServiceFactory.getRestClient(normalizedBaseUrl)
                .mutate()
                .requestInterceptor(authInterceptor)
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(apiType);
    }

}
