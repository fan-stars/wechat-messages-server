package cn.fanstars.module.system.framework.sms.rest.interceptor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
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
import java.util.TimeZone;

/**
 * 七牛云短信 Qiniu 鉴权拦截器
 * <p>
 * 签名逻辑与原 {@code QiniuSmsRpcExecutor} 一致。
 */
@RequiredArgsConstructor
public class QiniuSmsAuthInterceptor implements ClientHttpRequestInterceptor {

    private static final String HOST = "sms.qiniuapi.com";

    private final SmsChannelProperties properties;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String httpMethod = request.getMethod().name();
        String path = request.getURI().getPath();
        String bodyStr = body != null ? new String(body, StandardCharsets.UTF_8) : "";
        String signDate = DateUtil.date().setTimeZone(TimeZone.getTimeZone("UTC"))
                .toString("yyyyMMdd'T'HHmmss'Z'");
        String authorization = getSignature(httpMethod, path, bodyStr, signDate);

        HttpRequest signedRequest = new HttpRequestWrapper(request) {
            @Override
            public URI getURI() {
                return request.getURI();
            }

            @Override
            public org.springframework.http.HttpHeaders getHeaders() {
                org.springframework.http.HttpHeaders httpHeaders = new org.springframework.http.HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                httpHeaders.set("HOST", HOST);
                httpHeaders.set("Authorization", authorization);
                httpHeaders.set("Content-Type", "application/json");
                httpHeaders.set("X-Qiniu-Date", signDate);
                return httpHeaders;
            }
        };
        return execution.execute(signedRequest, body);
    }

    private String getSignature(String method, String path, String body, String signDate) {
        StringBuilder dataToSign = new StringBuilder();
        dataToSign.append(method.toUpperCase()).append(" ").append(path)
                .append("\nHost: ").append(HOST)
                .append("\nContent-Type: application/json")
                .append("\nX-Qiniu-Date: ").append(signDate)
                .append("\n\n");
        if (ObjectUtil.isNotEmpty(body)) {
            dataToSign.append(body);
        }
        String signature = SecureUtil.hmac(HmacAlgorithm.HmacSHA1, properties.getApiSecret())
                .digestBase64(dataToSign.toString(), true);
        return "Qiniu " + properties.getApiKey() + ":" + signature;
    }

}
