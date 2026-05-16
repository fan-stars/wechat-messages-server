package cn.fanstars.module.mp.framework.retrofit2.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Url;

import java.util.Map;

/**
 * 公众号消息透传转发 Retrofit API
 * <p>
 * 目标地址由规则配置，每条规则可能不同，因此使用 {@link Url} 传入完整 URL，
 * 而非固定 baseUrl + path。
 */
public interface MpMessageForwardApi {

    /**
     * Retrofit 要求 baseUrl 合法；实际请求走 {@link Url} 绝对地址，此处仅占位
     */
    String PLACEHOLDER_BASE_URL = "http://127.0.0.1/";

    /**
     * 将微信原始 XML 透传到下游
     *
     * @param url     完整目标 URL（含 signature、timestamp 等 query）
     * @param headers 透传请求头（Content-Type、X-Mp-Rule-Id 等）
     * @param body    微信 POST 原始 XML 体
     */
    @POST
    Call<ResponseBody> forward(@Url String url, @HeaderMap Map<String, String> headers, @Body RequestBody body);

}
