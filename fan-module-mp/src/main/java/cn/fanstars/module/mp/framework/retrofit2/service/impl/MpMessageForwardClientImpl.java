package cn.fanstars.module.mp.framework.retrofit2.service.impl;

import cn.fanstars.framework.okhttp.core.OkHttpFactory;
import cn.fanstars.module.mp.framework.retrofit2.api.MpMessageForwardApi;
import cn.fanstars.module.mp.framework.retrofit2.bo.MpMessageForwardHttpResultBO;
import cn.fanstars.module.mp.framework.retrofit2.service.MpMessageForwardClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 公众号消息透传转发 HTTP 客户端实现
 * <p>
 * 每条规则可配置不同 {@code timeoutMs}，因此按超时时间动态创建 OkHttpClient / Retrofit 实例。
 */
@Slf4j
@AllArgsConstructor
public class MpMessageForwardClientImpl implements MpMessageForwardClient {

    /**
     * 与微信回调一致的 XML Content-Type
     */
    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("application/xml; charset=UTF-8");

    private final OkHttpFactory okHttpFactory;

    @Override
    public MpMessageForwardHttpResultBO forward(String url, Map<String, String> headers,
                                                String rawContent, int timeoutMs) {
        // 按规则超时重建客户端（全局 OkHttp 默认超时无法满足每条规则）
        MpMessageForwardApi api = createApi(timeoutMs);
        RequestBody requestBody = RequestBody.create(rawContent, MEDIA_TYPE_XML);
        long start = System.currentTimeMillis();
        try {
            // 同步执行，供微信回调线程或 @Async 线程使用
            Response<ResponseBody> response = api.forward(url, headers, requestBody).execute();
            int durationMs = (int) (System.currentTimeMillis() - start);
            String responseBody = null;
            ResponseBody body = response.body();
            if (body != null) {
                try (body) {
                    responseBody = body.string(); // 下游被动回复 XML
                }
            }
            return MpMessageForwardHttpResultBO.success(responseBody, response.code(), durationMs);
        } catch (Exception ex) {
            int durationMs = (int) (System.currentTimeMillis() - start);
            log.warn("[forward][url({}) 转发失败]", url, ex);
            return MpMessageForwardHttpResultBO.failure(resolveErrorMsg(ex), durationMs);
        }
    }

    /**
     * 判断异常链中是否包含读/写超时
     */
    private static boolean isTimeoutException(Throwable ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof SocketTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    /**
     * 基于共享连接池创建带独立超时的 Retrofit 客户端
     */
    private MpMessageForwardApi createApi(int timeoutMs) {
        OkHttpClient client = okHttpFactory.getOkHttpClient().newBuilder()
                .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build();
        return new Retrofit.Builder()
                .baseUrl(MpMessageForwardApi.PLACEHOLDER_BASE_URL) // @Url 为绝对地址时会忽略 baseUrl
                .client(client)
                .build()
                .create(MpMessageForwardApi.class);
    }

    /**
     * 统一错误文案，便于上层识别超时并写入 TIMEOUT 日志状态
     */
    private static String resolveErrorMsg(Exception ex) {
        if (isTimeoutException(ex)) {
            return "timeout";
        }
        return ex.getMessage();
    }

}
