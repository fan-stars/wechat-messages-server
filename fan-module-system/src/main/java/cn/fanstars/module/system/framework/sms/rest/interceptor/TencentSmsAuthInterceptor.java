package cn.fanstars.module.system.framework.sms.rest.interceptor;

import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static cn.hutool.crypto.digest.DigestUtil.sha256Hex;

/**
 * 腾讯云短信 TC3-HMAC-SHA256 鉴权拦截器
 * <p>
 * {@code X-TC-Action} 来自 {@link TencentSmsRpcContext}；签名逻辑与原 {@code TencentSmsRpcExecutor} 一致。
 */
@RequiredArgsConstructor
public class TencentSmsAuthInterceptor implements ClientHttpRequestInterceptor {

    private static final String HOST = "sms.tencentcloudapi.com";
    private static final String VERSION = "2021-01-11";
    private static final String REGION = "ap-guangzhou";

    private final SmsChannelProperties properties;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        Assert.notEmpty(properties.getApiSecret(), "apiSecret 不能为空");

        String action = TencentSmsRpcContext.getAction();
        Assert.notEmpty(action, "X-TC-Action 未设置");

        String secretId = StrUtil.subBefore(properties.getApiKey(), " ", true);
        String bodyJson = body != null ? new String(body, StandardCharsets.UTF_8) : "";

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Host", HOST);
        headers.put("X-TC-Action", action);
        Date now = new Date();
        String nowStr = FastDateFormat.getInstance("yyyy-MM-dd", TimeZone.getTimeZone("UTC")).format(now);
        headers.put("X-TC-Timestamp", String.valueOf(now.getTime() / 1000));
        headers.put("X-TC-Version", VERSION);
        headers.put("X-TC-Region", REGION);

        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:application/json; charset=utf-8\n"
                + "host:" + HOST + "\n" + "x-tc-action:" + action.toLowerCase() + "\n";
        String signedHeaders = "content-type;host;x-tc-action";
        String canonicalRequest = "POST" + "\n" + "/" + "\n" + canonicalQueryString + "\n" + canonicalHeaders + "\n"
                + signedHeaders + "\n" + sha256Hex(bodyJson);
        String credentialScope = nowStr + "/sms/tc3_request";
        String stringToSign = "TC3-HMAC-SHA256" + "\n" + now.getTime() / 1000 + "\n" + credentialScope + "\n"
                + sha256Hex(canonicalRequest);
        byte[] secretService = hmac256(hmac256(("TC3" + properties.getApiSecret()).getBytes(StandardCharsets.UTF_8), nowStr), "sms");
        String signature = HexUtil.encodeHexStr(hmac256(hmac256(secretService, "tc3_request"), stringToSign));
        headers.put("Authorization", "TC3-HMAC-SHA256 Credential=" + secretId + "/" + credentialScope
                + ", SignedHeaders=" + signedHeaders + ", Signature=" + signature);

        HttpRequest signedRequest = new HttpRequestWrapper(request) {
            @Override
            public URI getURI() {
                return request.getURI();
            }

            @Override
            public org.springframework.http.HttpHeaders getHeaders() {
                org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                headers.forEach(httpHeaders::set);
                return httpHeaders;
            }
        };
        return execution.execute(signedRequest, body);
    }

    private static byte[] hmac256(byte[] key, String msg) {
        return DigestUtil.hmac(HmacAlgorithm.HmacSHA256, key).digest(msg);
    }

}
