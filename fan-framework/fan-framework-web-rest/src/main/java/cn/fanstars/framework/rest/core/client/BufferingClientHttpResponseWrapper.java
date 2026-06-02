package cn.fanstars.framework.rest.core.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 缓存响应体的 {@link ClientHttpResponse} 包装，支持 body 被多次读取
 * <p>
 * 典型场景：拦截器消费 {@link ClientHttpResponse#getBody()} 后，下游仍需要反序列化响应体。
 */
public record BufferingClientHttpResponseWrapper(ClientHttpResponse response,
                                                 byte[] body) implements ClientHttpResponse {

    public BufferingClientHttpResponseWrapper(@NonNull ClientHttpResponse response, @NonNull byte[] body) {
        this.response = response;
        this.body = body;
    }

    /**
     * 读取并缓存原始响应体，返回可重复读取的包装实例
     */
    @NonNull
    public static BufferingClientHttpResponseWrapper buffer(@NonNull ClientHttpResponse response) throws IOException {
        return new BufferingClientHttpResponseWrapper(response, StreamUtils.copyToByteArray(response.getBody()));
    }

    /**
     * 已缓存的响应体字节（请勿修改数组内容）
     */
    @NonNull
    public byte[] getBodyBytes() {
        return body;
    }

    @Override
    @NonNull
    public InputStream getBody() {
        return new ByteArrayInputStream(body);
    }

    @Override
    @NonNull
    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }

    @Override
    @NonNull
    public HttpStatusCode getStatusCode() throws IOException {
        return response.getStatusCode();
    }

    @Override
    @NonNull
    public String getStatusText() throws IOException {
        return response.getStatusText();
    }

    @Override
    public void close() {
        response.close();
    }

}
