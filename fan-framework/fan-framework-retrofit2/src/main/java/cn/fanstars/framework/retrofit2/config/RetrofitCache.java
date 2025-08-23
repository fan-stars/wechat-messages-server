package cn.fanstars.framework.retrofit2.config;

import java.util.concurrent.TimeUnit;

public interface RetrofitCache {

    String get(String key);

    void set(String key, String value);

    void increment(String key);

    default void expire(String key, final long timeout, final TimeUnit unit) {
    }

}
