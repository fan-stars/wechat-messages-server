package cn.fanstars.framework.rest.core.impl;

import cn.fanstars.framework.rest.config.RestClientProperties;
import cn.fanstars.framework.rest.core.HttpServiceFactory;
import lombok.AllArgsConstructor;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

/**
 * HttpServiceFactory 实现：JDK HttpClient + RestClient + HttpServiceProxyFactory
 */
@AllArgsConstructor
public class HttpServiceFactoryImpl implements HttpServiceFactory {

    /**
     * 直连 HttpClient 单例
     */
    private static HttpClient httpClient;
    /**
     * 代理 HttpClient 单例
     */
    private static HttpClient proxyHttpClient;

    private final RestClientProperties restClientProperties;
    /**
     * 由 FanRestAutoConfiguration 注入，供 HttpClient 异步 IO 使用
     */
    private final Executor executor;
    /**
     * 含 RestClientLoggingInterceptor 等 Spring 容器中的拦截器 Bean
     */
    private final List<ClientHttpRequestInterceptor> interceptors;

    /**
     * 按 baseUrl + 是否代理 缓存 RestClient
     */
    private final ConcurrentMap<String, RestClient> restClientCache = new ConcurrentHashMap<>();

    @Override
    public RestClient getRestClient(String baseUrl) {
        return getRestClient(baseUrl, false);
    }

    @Override
    public RestClient getRestClient(String baseUrl, boolean proxyFlag) {
        String cacheKey = baseUrl + "|" + proxyFlag;
        return restClientCache.computeIfAbsent(cacheKey, key -> buildRestClient(baseUrl, proxyFlag));
    }

    @Override
    public <T> T createHttpService(Class<T> serviceType, String baseUrl) {
        return createHttpService(serviceType, baseUrl, false);
    }

    @Override
    public <T> T createHttpService(Class<T> serviceType, String baseUrl, boolean proxyFlag) {
        RestClient restClient = getRestClient(baseUrl, proxyFlag);
        // 将 RestClient 适配为 HTTP Interface 的调用入口
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(serviceType); // 生成 @HttpExchange 接口实现
    }

    private RestClient buildRestClient(String baseUrl, boolean proxyFlag) {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(createRequestFactory(proxyFlag));
        if (!interceptors.isEmpty()) {
            interceptors.forEach(builder::requestInterceptor); // 如日志、鉴权头等扩展
        }
        return builder.build();
    }

    private ClientHttpRequestFactory createRequestFactory(boolean proxyFlag) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(getHttpClient(proxyFlag));
        requestFactory.setReadTimeout(Duration.ofSeconds(restClientProperties.getReadTimeout()));
        return requestFactory;
    }

    private synchronized HttpClient getHttpClient(boolean proxyFlag) {
        if (proxyFlag) {
            if (proxyHttpClient == null) {
                Proxy proxy = restClientProperties.getProxy();
                if (proxy == Proxy.NO_PROXY) {
                    proxyHttpClient = getHttpClient(false); // 未配置代理则复用直连客户端
                } else {
                    proxyHttpClient = createHttpClientBuilder(proxy).build();
                }
            }
            return proxyHttpClient;
        }
        if (httpClient == null) {
            httpClient = createHttpClientBuilder(null).build();
        }
        return httpClient;
    }

    private HttpClient.Builder createHttpClientBuilder(Proxy proxy) {
        HttpClient.Builder builder = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(restClientProperties.getConnectTimeout()))
                .executor(executor); // 使用 fanRestExecutor 线程池
        if (proxy != null && proxy != Proxy.NO_PROXY) {
            // JDK HttpClient.proxy() 需要 ProxySelector，不能直接传 Proxy
            builder.proxy(new ProxySelectorOf(proxy));
        }
        return builder;
    }

    /**
     * 固定返回单一 Proxy 的 ProxySelector，兼容 HTTP / SOCKS 类型
     */
    private static final class ProxySelectorOf extends ProxySelector {

        private final Proxy proxy;

        private ProxySelectorOf(Proxy proxy) {
            this.proxy = proxy;
        }

        @Override
        public List<Proxy> select(URI uri) {
            return Collections.singletonList(proxy);
        }

        @Override
        public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
            // 连接失败时不做额外处理
        }

    }

}
