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
 * 钉钉群机器人 Webhook 适配器
 *
 * @author 繁星源码
 */
@Component
public class DingTalkImAdapter implements ImPlatformAdapter {

    @Override
    public ImPlatformEnum getPlatform() {
        return ImPlatformEnum.DINGTALK;
    }

    @Override
    public String buildRequestUrl(ImWebhookDO webhook) {
        long timestamp = System.currentTimeMillis();
        String accessToken = webhook.getAccessToken();
        if (StrUtil.isBlank(webhook.getSecret())) {
            return String.format("https://oapi.dingtalk.com/robot/send?access_token=%s", accessToken);
        }
        String stringToSign = timestamp + "\n" + webhook.getSecret();
        byte[] signData = DigestUtil.hmac(HmacAlgorithm.HmacSHA256, StrUtil.bytes(webhook.getSecret()))
                .digest(stringToSign);
        String sign = Base64.encode(signData);
        return String.format("https://oapi.dingtalk.com/robot/send?access_token=%s&timestamp=%d&sign=%s",
                accessToken, timestamp, sign);
    }

    @Override
    public String buildPayload(ImWebhookDO webhook, String renderedContent, ImMsgTypeEnum msgType) {
        String content = ImAdapterUtils.truncateContent(renderedContent);
        Map<String, Object> params = new HashMap<>();
        if (msgType == ImMsgTypeEnum.MARKDOWN) {
            params.put("msgtype", "markdown");
            params.put("markdown", MapUtil.builder()
                    .put("title", "通知")
                    .put("text", content)
                    .build());
        } else {
            params.put("msgtype", "text");
            params.put("text", MapUtil.builder().put("content", content).build());
        }
        return JsonUtils.toJsonString(params);
    }

}
