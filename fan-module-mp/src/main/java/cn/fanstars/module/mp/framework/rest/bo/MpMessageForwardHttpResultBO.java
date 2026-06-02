package cn.fanstars.module.mp.framework.rest.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息转发 HTTP 调用结果（RestClient 层，不含业务日志状态）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpMessageForwardHttpResultBO {

    /**
     * 下游响应体（原始 XML 字符串）；未开启接收响应或调用失败时为 null
     */
    private String responseBody;
    /**
     * HTTP 状态码；网络异常未拿到响应时为 null
     */
    private Integer httpStatus;
    /**
     * 请求耗时（毫秒）
     */
    private int durationMs;
    /**
     * 异常信息；成功时为 null
     */
    private String errorMsg;

    public static MpMessageForwardHttpResultBO success(String responseBody, int httpStatus, int durationMs) {
        return new MpMessageForwardHttpResultBO(responseBody, httpStatus, durationMs, null);
    }

    public static MpMessageForwardHttpResultBO failure(String errorMsg, int durationMs) {
        return new MpMessageForwardHttpResultBO(null, null, durationMs, errorMsg);
    }

}
