package cn.fanstars.framework.rest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * RestClient / JDK HttpClient 配置，前缀 fan.rest
 */
@Data
@ConfigurationProperties(prefix = "fan.rest")
public class RestClientProperties {

    /**
     * 懒加载缓存的代理实例，与 fan-framework-retrofit2 行为一致
     */
    private static Proxy proxy = null;

    /**
     * 连接超时（秒）
     */
    private Long connectTimeout = 90L;
    /**
     * 读取超时（秒）
     */
    private Long readTimeout = 90L;
    /**
     * 代理类型：HTTP / SOCKS
     */
    private Proxy.Type proxyType;
    private String proxyIp;
    private Integer proxyPort;
    /**
     * HTTP 客户端线程池配置
     */
    private Pool pool = new Pool();

    /**
     * 获取代理；未配置时返回 {@link Proxy#NO_PROXY}
     */
    public synchronized Proxy getProxy() {
        if (proxyType == null || !StringUtils.hasLength(proxyIp) || proxyPort == null) {
            return Proxy.NO_PROXY;
        }
        if (proxy == null) {
            proxy = new Proxy(proxyType, new InetSocketAddress(proxyIp, proxyPort));
        }
        return proxy;
    }

    /**
     * JDK HttpClient 线程池参数，对应 fan.rest.pool.*
     */
    @Data
    public static class Pool {

        private Integer coreSize = 10;
        private Integer maxSize = 50;
        private Integer queueCapacity = 1000;
        private Integer keepAliveSeconds = 60;
        private String threadNamePrefix = "fan-rest-";

    }

}
