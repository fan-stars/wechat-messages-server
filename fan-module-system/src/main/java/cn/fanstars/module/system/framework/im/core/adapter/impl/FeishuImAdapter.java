package cn.fanstars.module.system.framework.im.core.adapter.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.fanstars.framework.common.util.json.JsonUtils;
import cn.fanstars.module.system.dal.dataobject.im.ImWebhookDO;
import cn.fanstars.module.system.framework.im.core.adapter.ImAdapterUtils;
import cn.fanstars.module.system.framework.im.core.adapter.ImPlatformAdapter;
import cn.fanstars.module.system.framework.im.core.enums.ImMsgTypeEnum;
import cn.fanstars.module.system.framework.im.core.enums.ImPlatformEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 飞书群机器人 Webhook 适配器
 * <p>
 * Phase 1 统一使用 text 消息；Markdown 降级为纯文本。
 * 启用签名校验时，请求体须带 timestamp、sign（飞书约定：HmacSHA256 密钥为 {@code timestamp + "\n" + secret}，消息体为空）。
 *
 * @author 繁星源码
 */
@Component
public class FeishuImAdapter implements ImPlatformAdapter {

    @Override
    public ImPlatformEnum getPlatform() {
        return ImPlatformEnum.FEISHU;
    }

    @Override
    public String buildRequestUrl(ImWebhookDO webhook) {
        return String.format("https://open.feishu.cn/open-apis/bot/v2/hook/%s", webhook.getAccessToken());
    }

    @Override
    public String buildPayload(ImWebhookDO webhook, String renderedContent, ImMsgTypeEnum msgType) {
        String content = ImAdapterUtils.truncateContent(renderedContent);
        Map<String, Object> params = new HashMap<>();
        if (StrUtil.isNotBlank(webhook.getSecret())) {
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String stringToSign = timestamp + "\n" + webhook.getSecret();
            byte[] signData = DigestUtil.hmac(HmacAlgorithm.HmacSHA256, StrUtil.bytes(stringToSign))
                    .digest(new byte[0]);
            params.put("timestamp", timestamp);
            params.put("sign", Base64.encode(signData));
        }
        params.put("msg_type", "text");
        params.put("content", MapUtil.builder().put("text", content).build());
        return JsonUtils.toJsonString(params);
    }

}
