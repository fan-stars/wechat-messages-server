package cn.fanstars.module.system.framework.im.core.client.dto;

import lombok.Data;

/**
 * IM Webhook 发送结果
 *
 * @author 繁星源码
 */
@Data
public class ImWebhookSendResult {

    private boolean success;
    private String apiCode;
    private String apiMsg;

    public static ImWebhookSendResult success(String apiCode, String apiMsg) {
        ImWebhookSendResult result = new ImWebhookSendResult();
        result.setSuccess(true);
        result.setApiCode(apiCode);
        result.setApiMsg(apiMsg);
        return result;
    }

    public static ImWebhookSendResult failure(String apiCode, String apiMsg) {
        ImWebhookSendResult result = new ImWebhookSendResult();
        result.setSuccess(false);
        result.setApiCode(apiCode);
        result.setApiMsg(apiMsg);
        return result;
    }

}
