package cn.fanstars.module.system.framework.im.core.adapter;

import cn.fanstars.module.system.dal.dataobject.im.ImWebhookDO;
import cn.fanstars.module.system.framework.im.core.enums.ImMsgTypeEnum;
import cn.fanstars.module.system.framework.im.core.enums.ImPlatformEnum;

/**
 * IM 平台 Webhook 适配器：统一正文 → 平台 JSON
 *
 * @author 繁星源码
 */
public interface ImPlatformAdapter {

    /**
     * 支持的平台
     */
    ImPlatformEnum getPlatform();

    /**
     * 构建 Webhook 请求 URL
     *
     * @param webhook Webhook 配置
     * @return 完整请求地址
     */
    String buildRequestUrl(ImWebhookDO webhook);

    /**
     * 将渲染后的正文包装为平台 Webhook JSON
     *
     * @param webhook         Webhook 配置（飞书加签等场景需要 secret）
     * @param renderedContent 已替换占位符的正文
     * @param msgType         逻辑消息类型
     * @return JSON 字符串
     */
    String buildPayload(ImWebhookDO webhook, String renderedContent, ImMsgTypeEnum msgType);

}
