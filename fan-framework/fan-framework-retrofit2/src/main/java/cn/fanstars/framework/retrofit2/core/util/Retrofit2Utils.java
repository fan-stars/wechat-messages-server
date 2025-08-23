package cn.fanstars.framework.retrofit2.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.Map;

public class Retrofit2Utils {

    public static Map<String, Object> parseMap(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }
        String objStr;
        if (obj instanceof String) {
            objStr = (String) obj;
        } else {
            objStr = JSON.toJSONString(obj);
        }
        return JSON.parseObject(objStr, new TypeReference<Map<String, Object>>(){});
    }

}
