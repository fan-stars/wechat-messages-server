package cn.fanstars.framework.okhttp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Data
@ConfigurationProperties(prefix = "fan.ok-http")
public class OkHttpProperties {

    private static Proxy proxy = null;

    private Long connectTimeout = 90L;
    private Long writeTimeout = 90L;
    private Long readTimeout = 90L;
    private Integer maxIdleConnections = 5;
    private Integer keepAliveDuration = 10;
    private Proxy.Type proxyType;
    private String proxyIp;
    private Integer proxyPort;

    public synchronized Proxy getProxy() {
        if (proxyType == null || !StringUtils.hasLength(proxyIp) || proxyPort == null) {
            return Proxy.NO_PROXY;
        }
        if (proxy == null) {
            proxy = new Proxy(proxyType, new InetSocketAddress(proxyIp, proxyPort));
        }
        return proxy;
    }

}
