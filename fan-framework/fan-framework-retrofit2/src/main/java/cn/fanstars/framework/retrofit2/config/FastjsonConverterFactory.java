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

@Slf4j
public class FastjsonConverterFactory extends Converter.Factory {

    private static FastjsonConverterFactory converter;

    private static FastjsonConverterFactory compareConverter;

    /**
     * 是否对比结果
     */
    private final boolean isCompareResult = !SpringUtils.isProd();

    private FastjsonConverterFactory() {
    }

    public static synchronized FastjsonConverterFactory create() {
        if (converter == null) {
            converter = new FastjsonConverterFactory();
        }
        return converter;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(@NotNull Type type, @NotNull Annotation[] annotations,
                                                            @NotNull Retrofit retrofit) {
        return (Converter<ResponseBody, Object>) value -> {
            String responseString = "";
            try (value) {
                responseString = value.string();
                Object converterResponse = JSON.parseObject(responseString, type);
                if (isCompareResult) {

                    String originResult = JSON.toJSONString(JSONObject.parse(responseString), SerializerFeature.MapSortField);
                    String converterResult = JSON.toJSONString(converterResponse, SerializerFeature.MapSortField);
                    if (!originResult.equals(converterResult)) {
                        log.info("originResult:    {}", originResult);
                        log.info("converterResult: {}", converterResult);
                    }
                }
                return converterResponse;
            } catch (Exception e) {
                log.info("responseBodyConverter is error: ", e);
                return responseString;
            }
        };
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(@NotNull Type type, @NotNull Annotation[] parameterAnnotations,
                                                          @NotNull Annotation[] methodAnnotations, @NotNull Retrofit retrofit) {
        return (Converter<Object, RequestBody>) value -> {
            String jsonString = JSON.toJSONString(value);
            return RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), jsonString);
        };
    }
}
