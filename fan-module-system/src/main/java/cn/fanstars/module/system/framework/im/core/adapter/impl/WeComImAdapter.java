package cn.fanstars.module.system.framework.im.core.adapter.impl;

import cn.hutool.core.map.MapUtil;
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
 * 企业微信群机器人 Webhook 适配器
 *
 * @author 繁星源码
 */
@Component
public class WeComImAdapter implements ImPlatformAdapter {

    @Override
    public ImPlatformEnum getPlatform() {
        return ImPlatformEnum.WECOM;
    }

    @Override
    public String buildRequestUrl(ImWebhookDO webhook) {
        return String.format("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s", webhook.getAccessToken());
    }

    @Override
    public String buildPayload(ImWebhookDO webhook, String renderedContent, ImMsgTypeEnum msgType) {
        String content = ImAdapterUtils.truncateContent(renderedContent);
        Map<String, Object> params = new HashMap<>();
        // 企微 Markdown 为子集；Phase 1 对 Markdown 类型仍尝试 markdown，失败由发送层记日志
        if (msgType == ImMsgTypeEnum.MARKDOWN) {
            params.put("msgtype", "markdown");
            params.put("markdown", MapUtil.builder().put("content", content).build());
        } else {
            params.put("msgtype", "text");
            params.put("text", MapUtil.builder().put("content", content).build());
        }
        return JsonUtils.toJsonString(params);
    }

}
