package cn.fanstars.framework.rest.core.util;

import cn.fanstars.framework.common.util.spring.SpringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Fastjson 响应体比对工具，逻辑与 retrofit2 {@code FastjsonConverterFactory} 非生产环境对比一致
 * <p>
 * 用于排查「原始 JSON」与「Fastjson 解析后再序列化」不一致的问题（类型、精度、字段丢失等）。
 */
@Slf4j
public final class FastjsonResponseCompareUtils {

    /**
     * 深度对比时的根路径前缀，子字段拼接为 "{}.fieldName"
     */
    private static final String ROOT = "{}";

    private FastjsonResponseCompareUtils() {
    }

    /**
     * 非生产环境：对比原始 JSON 与 Fastjson 解析后再序列化的结果，不一致时递归打印字段差异
     */
    public static void compareIfNeeded(String responseBody) {
        if (SpringUtils.isProd() || !StringUtils.hasText(responseBody)) {
            return; // 生产环境关闭，避免额外开销
        }
        try {
            // 原始串 → JSONObject → 排序序列化
            String originResult = JSON.toJSONString(JSONObject.parse(responseBody), SerializerFeature.MapSortField);
            // 模拟通用解析路径（无具体 Type 时与 parse 行为一致）
            Object parsed = JSON.parse(responseBody);
            String converterResult = parsed instanceof String ? parsed.toString()
                    : JSON.toJSONString(parsed, SerializerFeature.MapSortField);
            if (!originResult.equals(converterResult)) {
                log.info("[RestClient] fastjson compare originResult:    {}", originResult);
                log.info("[RestClient] fastjson compare converterResult: {}", converterResult);
                deepCompare(ROOT, originResult, converterResult); // 定位到具体字段
            }
        } catch (Exception e) {
            log.info("[RestClient] fastjson compare error: ", e);
        }
    }

    /**
     * 递归逐字段对比，与 FastjsonConverterFactory#deepCompare 相同
     */
    private static void deepCompare(String keyPrev, String originResult, String converterResult) {
        Object originObj = JSONObject.parse(originResult);
        Object converterObj = JSONObject.parse(converterResult);
        if (originObj instanceof JSONObject origin) {
            if (!(converterObj instanceof JSONObject converter)) {
                // 一侧为对象、一侧非对象，直接打印当前路径
                log.info("[RestClient] key: {}, originResult:    {}", keyPrev, originResult);
                log.info("[RestClient] key: {}, converterResult: {}", keyPrev, converterResult);
                return;
            }
            origin.forEach((key, value) -> {
                String currentPath = keyPrev + "." + key;
                deepCompare(currentPath, JSON.toJSONString(value), JSON.toJSONString(converter.get(key)));
            });
            if (keyPrev.equals(ROOT)) {
                return; // 根节点差异已在子路径中输出
            }
            if (!originObj.equals(converterObj)) {
                log.info("[RestClient] key: {}, originResult:    {}", keyPrev, originResult);
                log.info("[RestClient] key: {}, converterResult: {}", keyPrev, converterResult);
            }
        }
    }

}
