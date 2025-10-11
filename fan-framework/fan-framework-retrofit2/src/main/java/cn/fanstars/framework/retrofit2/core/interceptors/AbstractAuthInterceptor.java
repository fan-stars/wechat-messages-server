package cn.fanstars.framework.retrofit2.core.interceptors;

import cn.fanstars.framework.retrofit2.config.RetrofitCache;
import cn.fanstars.framework.retrofit2.core.enums.CacheKeyConstants;
import cn.fanstars.framework.retrofit2.core.pojo.ApiInfoDTO;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractAuthInterceptor implements Interceptor, AuthInterceptor {

    protected final ApiInfoDTO apiInfoDTO;

    protected final RetrofitCache retrofitCache;

    public AbstractAuthInterceptor(ApiInfoDTO apiInfoDTO, RetrofitCache retrofitCache) {
        this.apiInfoDTO = apiInfoDTO;
        this.retrofitCache = retrofitCache;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = addAuthRequest(chain.request());

        Response response = chain.proceed(originalRequest);

        boolean isNoLogin = false;
        try {
            int code = response.code();
            if (code == 404 || code == 500) {
                log.info("response code: {}", code);
                return response;
            }
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                // 使用 BufferedSource 读取响应体
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);  // 请求数据，确保缓冲区包含整个响应体
                Buffer buffer = source.getBuffer();
                // 获取默认的 charset（如 UTF-8）
                Charset charset = StandardCharsets.UTF_8;
                MediaType contentType = responseBody.contentType();
                if (contentType != null && contentType.charset() != null) {
                    charset = contentType.charset(); // 仅当 charset 存在时使用
                }
                if (charset == null) {
                    return response;
                }
                String body = buffer.clone().readString(charset);
                if (!StringUtils.hasLength(body)) {
                    return response;
                }

                log.debug("response body: " + body);
                try {
                    isNoLogin = checkLoginStatus(body);
                } catch (Exception e) {
                    log.info("check login status is fail, body: {}", body);
                    log.error("parse response body is error: ", e);
                }
            }
        } catch (Exception e) {
            log.info("parse response body is error: ", e);
        }

        if (isNoLogin) {
            return tryLoginThenReRequest(chain, response, originalRequest);
        }

        // 返回原始响应
        return response;
    }

    private Response tryLoginThenReRequest(Chain chain, Response response, Request originalRequest) {
        String key = String.format(CacheKeyConstants.RETROFIT_INTERCEPTOR_AUTH_FAIL_KEY, getInterceptorUniqueValue());
        try {
            String value = retrofitCache.get(key);
            if (StringUtils.hasLength(value) && Integer.parseInt(value) >= getMaxAuthFailCount()) {
                log.info("{} {} {} 短时间请求授权接口失败已达 {} 次，暂时不在请求，返回原来的 Response", apiInfoDTO.getApiHost(),
                        apiInfoDTO.getUsername(), getInterceptorUniqueValue(), getMaxAuthFailCount());
                return response;
            }
            boolean flag = login();
            if (flag) {
                // 关闭原始响应
                response.close();
                // 使用新的Request重新执行原始请求
                Request newRequest = addAuthRequest(originalRequest);
                return chain.proceed(newRequest);
            }
        } catch (Exception e) {
            log.info("{} tryLogin is fail: ", getInterceptorUniqueValue(), e);
        }
        retrofitCache.increment(key);
        retrofitCache.expire(key, 300, TimeUnit.SECONDS);
        // 登录失败，返回原始响应
        return response;
    }

    protected String getInterceptorUniqueValue() {
        return MD5.create().digestHex(JSON.toJSONString(apiInfoDTO));
    }

    protected int getMaxAuthFailCount() {
        return 3;
    }

}
