package cn.fanstars.framework.rest.core;

import org.springframework.web.client.RestClient;

/**
 * 声明式 HTTP 客户端工厂，基于 Spring 6 {@code RestClient} + {@code @HttpExchange}
 */
public interface HttpServiceFactory {

    /**
     * 直连模式 RestClient
     */
    RestClient getRestClient(String baseUrl);

    /**
     * @param proxyFlag true 时使用 fan.rest 配置的代理
     */
    RestClient getRestClient(String baseUrl, boolean proxyFlag);

    /**
     * 创建 @HttpExchange 接口代理（直连）
     */
    <T> T createHttpService(Class<T> serviceType, String baseUrl);

    /**
     * 创建 @HttpExchange 接口代理
     *
     * @param proxyFlag true 时走代理 HttpClient
     */
    <T> T createHttpService(Class<T> serviceType, String baseUrl, boolean proxyFlag);

}
