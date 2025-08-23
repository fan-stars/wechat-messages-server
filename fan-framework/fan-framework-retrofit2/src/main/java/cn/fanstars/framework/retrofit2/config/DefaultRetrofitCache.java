package cn.fanstars.framework.retrofit2.config;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class DefaultRetrofitCache implements RetrofitCache {

    private final Map<String, String> cache = new HashMap<>();

    @Override
    public String get(String key) {
        String value = cache.get(key);
        return StringUtils.hasLength(value) ? value : "";
    }

    @Override
    public void set(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public void increment(String key) {
        String value = cache.get(key);
        if (StringUtils.hasLength(value)) {
            long l = Long.parseLong(value);
            cache.put(key, String.valueOf(l + 1));
        } else {
            cache.put(key, "1");
        }
    }

}
