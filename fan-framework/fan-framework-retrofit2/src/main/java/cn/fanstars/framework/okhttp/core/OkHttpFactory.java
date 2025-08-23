package cn.fanstars.framework.okhttp.core;

import okhttp3.OkHttpClient;

public interface OkHttpFactory {

    OkHttpClient getOkHttpClient();

    OkHttpClient getProxyOkHttpClient();

    OkHttpClient getOkHttpClient(boolean proxyFlag);

}
