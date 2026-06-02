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
     * 懒加载缓存的代理实例
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
     * RestClient 日志配置，对应 fan.rest.log.*
     */
    private Log log = new Log();

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

    /**
     * RestClient 全局日志拦截器配置
     */
    @Data
    public static class Log {

        /**
         * 是否打印请求/响应日志（false 时不注册日志拦截器）
         */
        private Boolean enabled = true;
        /**
         * 是否执行 Fastjson 响应比对（仅非 prod 且启用 Fastjson 消息转换器时生效）
         */
        private Boolean compareEnabled = true;
        /**
         * 日志中 body 最大打印长度，超出截断
         */
        private Integer maxBodyLength = 2048;

    }

}
