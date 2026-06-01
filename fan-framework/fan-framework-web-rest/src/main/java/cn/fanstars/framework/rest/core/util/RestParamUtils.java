package cn.fanstars.framework.rest.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * RestClient 请求参数 Map 工具
 * <p>
 * 将 VO 转为 {@code Map}，供 {@code @RequestParam Map}、表单字段拼装等使用。
 * 字段名取自 VO 上的 {@code @JSONField}（经 JSON 序列化/反序列化一轮，与 Retrofit 时代 {@code Retrofit2Utils#parseMap} 行为一致）。
 * 通常与 {@link cn.fanstars.framework.rest.core.converters.FastJsonRestMessageConverters} 搭配使用。
 */
public final class RestParamUtils {

    private RestParamUtils() {
    }

    /**
     * 将对象转为 {@code Map<String, Object>}，key 为 API 侧字段名（非 Java 属性名）
     *
     * @param obj VO、已有 JSON 字符串等；null 时返回空 Map
     */
    public static Map<String, Object> parseMap(Object obj) {
        if (obj == null) {
            return new HashMap<>();
        }
        String objStr;
        if (obj instanceof String) {
            objStr = (String) obj;
        } else {
            objStr = JSON.toJSONString(obj); // 先序列化再反序列化为 Map，保留 @JSONField 命名
        }
        return JSON.parseObject(objStr, new TypeReference<Map<String, Object>>() {
        });
    }

    /**
     * 合并两组 Query/表单参数（等价于 Retrofit 双 {@code @QueryMap}）
     * <p>
     * {@code extra} 中同名 key 覆盖 {@code base}；任一为 null 时按空 Map 处理
     */
    public static Map<String, Object> mergeParams(Map<String, Object> base, Map<String, Object> extra) {
        Map<String, Object> merged = new HashMap<>(base != null ? base : Map.of());
        if (extra != null) {
            merged.putAll(extra);
        }
        return merged;
    }

    /**
     * {@code Map} → Spring {@link MultiValueMap}，用于 {@code application/x-www-form-urlencoded}
     * <p>
     * null 值会被跳过
     */
    public static MultiValueMap<String, String> toFormMap(Map<String, Object> formData) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        if (formData == null) {
            return map;
        }
        formData.forEach((key, value) -> {
            if (value != null) {
                map.add(key, String.valueOf(value));
            }
        });
        return map;
    }

    /**
     * 将 Map 中某一嵌套字段展开为 {@code prefix[key]=value} 表单键（智简魔方等上游常见写法）
     *
     * @param formData  表单 Map，会被原地修改
     * @param nestedKey 嵌套字段名，如 {@code configoption}
     */
    public static void expandNestedFormField(Map<String, Object> formData, String nestedKey) {
        if (formData == null || nestedKey == null) {
            return;
        }
        Object nested = formData.remove(nestedKey);
        if (nested instanceof Map<?, ?> nestedMap) {
            for (Map.Entry<?, ?> entry : nestedMap.entrySet()) {
                formData.put(nestedKey + "[" + entry.getKey() + "]", entry.getValue());
            }
        }
    }

    /**
     * 将列表展开为 {@code prefix[0]}、{@code prefix[1]}... 表单键
     */
    public static void putIndexedList(Map<String, Object> formData, String prefix, Iterable<?> values) {
        if (formData == null || prefix == null || values == null) {
            return;
        }
        int i = 0;
        for (Object value : values) {
            formData.put(prefix + "[" + i + "]", value);
            i++;
        }
    }

}
