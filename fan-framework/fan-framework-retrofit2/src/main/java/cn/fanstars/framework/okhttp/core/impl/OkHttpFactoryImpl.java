package cn.fanstars.framework.okhttp.core.impl;

import cn.fanstars.framework.okhttp.config.OkHttpProperties;
import cn.fanstars.framework.okhttp.core.OkHttpFactory;
import lombok.AllArgsConstructor;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;

import java.net.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class OkHttpFactoryImpl implements OkHttpFactory {

    private static OkHttpClient okHttpClient;
    private static OkHttpClient proxyOkHttpClient;

    private final OkHttpProperties okHttpProperties;

    private final List<Interceptor> interceptors;

    @Override
    public synchronized OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = createOkHttpClientBuilder().proxy(Proxy.NO_PROXY).build();
        }
        return okHttpClient;
    }

    @Override
    public synchronized OkHttpClient getProxyOkHttpClient() {
        if (proxyOkHttpClient == null) {
            Proxy proxy = okHttpProperties.getProxy();
            if (okHttpProperties.getProxy() == Proxy.NO_PROXY) {
                proxyOkHttpClient = okHttpClient;
            } else {
                proxyOkHttpClient = createOkHttpClientBuilder().proxy(proxy).build();
            }
        }
        return proxyOkHttpClient;
    }

    @Override
    public OkHttpClient getOkHttpClient(boolean proxyFlag) {
        return proxyFlag ? getProxyOkHttpClient() : getOkHttpClient();
    }

    private OkHttpClient.Builder createOkHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(okHttpProperties.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(okHttpProperties.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(okHttpProperties.getReadTimeout(), TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(okHttpProperties.getMaxIdleConnections(),
                        okHttpProperties.getKeepAliveDuration(), TimeUnit.MINUTES));
        if (!interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        return builder;
    }

}
