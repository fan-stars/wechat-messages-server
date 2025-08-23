package cn.fanstars.framework.retrofit2.core.interceptors;

import okhttp3.Request;

public interface AuthInterceptor {

    Request addAuthRequest(Request originalRequest);

    boolean checkLoginStatus(String body);

    boolean login();

}
