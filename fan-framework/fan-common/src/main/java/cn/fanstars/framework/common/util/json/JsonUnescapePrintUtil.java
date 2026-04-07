package cn.fanstars.framework.common.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JSON 转义字符串递归解析工具类
 * <p>
 * 用于处理多层嵌套转义的 JSON 字符串，将字符串类型的 JSON 自动转换为
 * JSONObject/JSONArray，以便于查看和调试。
 * <p>
 * 常见场景：某些接口返回的 JSON 中，部分字段值是转义后的 JSON 字符串，
 * 直接打印只能看到转义后的字符串，无法直观查看嵌套内容。使用本工具
 * 可以递归展开所有嵌套的 JSON 字符串，使其结构化输出。
 *
 * <p>示例：
 * <pre>
 * // 处理前：{"password":"{\"show\":\"1\",\"modify\":0}"}
 * // 处理后：
 * // {
 * //     "password": {
 * //         "show": "1",
 * //         "modify": 0
 * //     }
 * // }
 * </pre>
 *
 * @author fan
 */
public class JsonUnescapePrintUtil {

    public static void main(String[] args) {
        // 待处理的完整JSON字符串（包含多层嵌套转义的JSON字段）
        String jsonStr = "...";

        // 步骤1: 将JSON字符串解析为 JSONObject 对象
        JSONObject root = JSON.parseObject(jsonStr);

        // 步骤2: 递归展开所有转义的JSON字符串（将字符串类型的JSON解析为真实对象）
        recursiveUnescapeJson(root);

        // 步骤3: 美化格式化输出（PrettyFormat 使JSON易读）
        String prettyJson = JSON.toJSONString(root, SerializerFeature.PrettyFormat);

        System.out.println(prettyJson);
    }

    /**
     * 递归解析：将字符串类型的转义 JSON 自动转为 JSONObject/JSONArray
     * <p>
     * 遍历 JSON 对象的每一个字段，如果值是字符串类型且内容是合法的 JSON
     *（以 {\@ 或 [ 开头），则将其解析为对应的 JSONObject 或 JSONArray，
     * 并继续递归处理，直到所有嵌套层级的 JSON 字符串都被展开。
     *
     * @param obj 要处理的 JSON 对象或数组，支持 JSONObject 和 JSONArray
     */
    public static void recursiveUnescapeJson(Object obj) {
        // 递归终止条件：空对象直接返回
        if (obj == null) return;

        // 分支处理：JSONObject 和 JSONArray 采用不同的遍历方式
        if (obj instanceof JSONObject jsonObj) {
            // 遍历 JSONObject 的每个键值对
            jsonObj.forEach((key, value) -> {
                // 判断当前值是否为字符串类型（转义JSON通常是字符串）
                if (value instanceof String strValue) {
                    // 尝试将字符串解析为JSON对象或数组
                    Object parseObj = tryParseJson(strValue);
                    // 解析成功（非null）则替换原值，并继续递归展开
                    if (parseObj != null) {
                        jsonObj.put(key, parseObj);       // 替换为解析后的真实对象
                        recursiveUnescapeJson(parseObj);  // 递归处理新展开的对象
                    }
                } else {
                    // 非字符串类型（如嵌套的JSONObject/JSONArray），直接递归处理
                    recursiveUnescapeJson(value);
                }
            });
        } else if (obj instanceof JSONArray array) {
            // 遍历 JSONArray 的每个元素
            array.forEach(item -> {
                // 处理数组成员为字符串的情况
                if (item instanceof String strValue) {
                    Object parseObj = tryParseJson(strValue);
                    if (parseObj != null) {
                        // 找到该元素在数组中的索引位置并替换
                        array.set(array.indexOf(item), parseObj);
                    }
                }
                // 递归处理数组成员（无论是否是字符串解析后的结果）
                recursiveUnescapeJson(item);
            });
        }
    }

    /**
     * 尝试将字符串解析为 JSON 对象或数组
     * <p>
     * 根据字符串的首字符判断其 JSON 类型：
     * <ul>
     *   <li>以 {@code { 开头 → 解析为 JSONObject</li>
     *   <li>以 {@code [ 开头 → 解析为 JSONArray</li>
     *   <li>其他情况 → 返回 null，表示不是合法 JSON</li>
     * </ul>
     *
     * @param str 要解析的字符串
     * @return 解析后的 JSONObject 或 JSONArray，如果解析失败则返回 null
     */
    public static Object tryParseJson(String str) {
        try {
            // 以 { 开头 → JSON对象
            if (str.startsWith("{")) {
                return JSON.parseObject(str);
            }
            // 以 [ 开头 → JSON数组
            else if (str.startsWith("[")) {
                return JSON.parseArray(str);
            }
        } catch (Exception ignored) {
            // 解析异常说明不是合法JSON字符串，返回null
        }
        return null;
    }
}