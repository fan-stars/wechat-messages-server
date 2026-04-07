package cn.fanstars.framework.retrofit2.config;

import cn.fanstars.framework.common.util.spring.SpringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Fastjson 转换器工厂
 * <p>
 * 用于 Retrofit2 的响应体和请求体的 JSON 序列化/反序列化处理。
 * 支持将 JSON 字符串自动转换为目标类型对象，以及将对象序列化为 JSON 请求体。
 *
 * @author fan
 * @see Converter.Factory
 */
@Slf4j
public class FastjsonConverterFactory extends Converter.Factory {

    /**
     * 根节点路径标识，用于深度对比时的路径拼接
     * <p>
     * 对比结果中展示路径时会拼接成 "user.info.name" 格式，
     * 使用 "{}" 作为根节点的前缀标识
     */
    private final static String ROOT = "{}";

    private static FastjsonConverterFactory converter;

    /**
     * 是否启用解析结果对比模式
     * <p>
     * 仅在非生产环境下启用，用于排查 Fastjson 解析异常问题。
     * 启用后会对比原始JSON字符串与解析后的差异，帮助定位解析问题。
     */
    private final boolean isCompareResult = !SpringUtils.isProd();

    private FastjsonConverterFactory() {
    }

    /**
     * 获取或创建 FastjsonConverterFactory 单例实例
     *
     * @return FastjsonConverterFactory 实例
     */
    public static synchronized FastjsonConverterFactory create() {
        if (converter == null) {
            converter = new FastjsonConverterFactory();
        }
        return converter;
    }

    /**
     * 响应体转换器：将 ResponseBody 转换为目标类型对象
     * <p>
     * 使用 Fastjson 将 HTTP 响应的 JSON 字符串解析为指定的 Java 类型。
     * 在非生产环境下会启用对比模式，记录解析差异。
     */
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NotNull Type type, @NotNull Annotation[] annotations,
                                                            @NotNull Retrofit retrofit) {
        return (Converter<ResponseBody, Object>) value -> {
            String responseString = "";
            try (value) {
                // 读取响应体内容为字符串
                responseString = value.string();

                // 使用 Fastjson 将 JSON 字符串解析为目标类型
                Object converterResponse = JSON.parseObject(responseString, type);

                // 非生产环境：对比原始JSON与解析结果的差异
                if (isCompareResult) {
                    // 将原始响应解析为 JSONObject 后序列化（用于对比）
                    String originResult = JSON.toJSONString(JSONObject.parse(responseString), SerializerFeature.MapSortField);

                    // 解析后的对象重新序列化（如果已是字符串则直接toString）
                    String converterResult = converterResponse instanceof String ? converterResponse.toString() :
                            JSON.toJSONString(converterResponse, SerializerFeature.MapSortField);

                    // 对比结果不一致时，记录并深度分析差异
                    if (!originResult.equals(converterResult)) {
                        log.info("originResult:    {}", originResult);
                        log.info("converterResult: {}", converterResult);
                        // 递归对比每个字段，找出具体差异位置
                        deepCompare(ROOT, originResult, converterResult);
                    }
                }
                return converterResponse;
            } catch (Exception e) {
                // 解析异常时返回原始字符串，避免业务中断
                log.info("responseBodyConverter is error: ", e);
                return responseString;
            }
        };
    }

    /**
     * 请求体转换器：将 Java 对象转换为 JSON 请求体
     * <p>
     * 将 Java 对象序列化为 JSON 字符串，用于 HTTP 请求体。
     */
    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NotNull Type type, @NotNull Annotation[] parameterAnnotations,
                                                          @NotNull Annotation[] methodAnnotations, @NotNull Retrofit retrofit) {
        return (Converter<Object, RequestBody>) value -> {
            // 将 Java 对象序列化为 JSON 字符串
            String jsonString = JSON.toJSONString(value);
            // 创建符合 RESTful 规范的请求体
            return RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), jsonString);
        };
    }

    /**
     * 深度对比两个 JSON 对象的差异
     * <p>
     * 当 Fastjson 解析结果与原始 JSON 字符串不一致时，递归遍历逐层对比，
     * 精确定位到具体字段级别，帮助排查解析问题。
     *
     * @param keyPrev         当前对比的路径前缀（如 "user.info.name"），用于定位差异位置
     * @param originResult    原始 JSON 字符串排序后的结果
     * @param converterResult  Fastjson 解析后再序列化排序后的结果
     */
    private void deepCompare(String keyPrev, String originResult, String converterResult) {
        // 分别解析两个 JSON 字符串为 JSONObject，便于遍历字段
        Object originObj = JSONObject.parse(originResult);
        Object converterObj = JSONObject.parse(converterResult);

        // 仅处理 JSON 对象类型（跳过数组等）
        if (originObj instanceof JSONObject origin) {
            // 如果解析结果不是 JSONObject，说明类型转换失败，记录差异
            if (!(converterObj instanceof JSONObject converter)) {
                log.info("key: {}, originResult:    {}", keyPrev, originResult);
                log.info("key: {}, converterResult: {}", keyPrev, converterResult);
                return;
            }

            // 遍历原始对象的每个字段，递归对比
            origin.forEach((key, value) -> {
                // 拼接当前字段的完整路径（如 "user.info.name"）
                String currentPath = keyPrev + "." + key;
                // 递归对比当前字段的值（序列化为字符串后对比）
                deepCompare(currentPath, JSON.toJSONString(value), JSON.toJSONString(converter.get(key)));
            });

            // 根节点不需要重复记录差异（已在子节点记录）
            if (keyPrev.equals(ROOT)) {
                return;
            }

            // 字段数量不一致时，记录整体差异
            if (!originObj.equals(converterObj)) {
                log.info("key: {}, originResult:    {}", keyPrev, originResult);
                log.info("key: {}, converterResult: {}", keyPrev, converterResult);
            }
        }
    }

}