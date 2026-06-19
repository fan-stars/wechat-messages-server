package cn.fanstars.module.system.framework.sms.rest.interceptor;

import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * 阿里云短信 ACS3-HMAC-SHA256 鉴权拦截器
 * <p>
 * 签名逻辑与 {@code AliyunSmsClient} / 原 {@code AliyunSmsRpcExecutor} 一致；{@code x-acs-action} 来自 {@link AliyunSmsRpcContext}。
 */
@RequiredArgsConstructor
public class AliyunSmsAuthInterceptor implements ClientHttpRequestInterceptor {

    private static final String HOST = "dysmsapi.aliyuncs.com";
    private static final String VERSION = "2017-05-25";

    private final SmsChannelProperties properties;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        Assert.notEmpty(properties.getApiKey(), "apiKey 不能为空");
        Assert.notEmpty(properties.getApiSecret(), "apiSecret 不能为空");

        String action = AliyunSmsRpcContext.getAction();
        Assert.notEmpty(action, "x-acs-action 未设置");

        String queryString = request.getURI().getRawQuery();
        if (queryString == null) {
            queryString = "";
        }
        String requestBody = "";
        String hashedRequestPayload = DigestUtil.sha256Hex(requestBody);

        TreeMap<String, String> headers = new TreeMap<>();
        headers.put("host", HOST);
        headers.put("x-acs-version", VERSION);
        headers.put("x-acs-action", action);
        headers.put("x-acs-date", FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss'Z'",
                TimeZone.getTimeZone("GMT")).format(new Date()));
        headers.put("x-acs-signature-nonce", IdUtil.randomUUID());
        headers.put("x-acs-content-sha256", hashedRequestPayload);

        StringBuilder canonicalHeaders = new StringBuilder();
        StringBuilder signedHeadersBuilder = new StringBuilder();
        headers.entrySet().stream()
                .filter(entry -> entry.getKey().toLowerCase().startsWith("x-acs-")
                        || "host".equalsIgnoreCase(entry.getKey())
                        || "content-type".equalsIgnoreCase(entry.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String lowerKey = entry.getKey().toLowerCase();
                    canonicalHeaders.append(lowerKey).append(":").append(String.valueOf(entry.getValue()).trim()).append("\n");
                    signedHeadersBuilder.append(lowerKey).append(";");
                });
        String signedHeaders = signedHeadersBuilder.substring(0, signedHeadersBuilder.length() - 1);

        String canonicalRequest = "POST" + "\n" + "/" + "\n" + queryString + "\n"
                + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedRequestPayload;
        String hashedCanonicalRequest = DigestUtil.sha256Hex(canonicalRequest);
        String stringToSign = "ACS3-HMAC-SHA256" + "\n" + hashedCanonicalRequest;
        String signature = SecureUtil.hmacSha256(properties.getApiSecret()).digestHex(stringToSign);
        headers.put("Authorization", "ACS3-HMAC-SHA256 Credential=" + properties.getApiKey()
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

}
