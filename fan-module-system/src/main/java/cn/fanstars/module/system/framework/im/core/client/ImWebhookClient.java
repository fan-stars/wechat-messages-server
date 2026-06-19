package cn.fanstars.module.system.framework.im.core.client;

import cn.fanstars.module.system.framework.im.core.client.dto.ImWebhookSendResult;

/**
 * IM Webhook HTTP 客户端
 *
 * @author 繁星源码
 */
public interface ImWebhookClient {

    /**
     * 发送 Webhook 请求
     *
     * @param url         请求地址
     * @param jsonBody    JSON 请求体
     * @return 发送结果
     */
    ImWebhookSendResult send(String url, String jsonBody);

}
