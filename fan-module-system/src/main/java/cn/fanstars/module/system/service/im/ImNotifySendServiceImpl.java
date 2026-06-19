package cn.fanstars.module.system.service.im;

import cn.hutool.core.collection.CollUtil;
import cn.fanstars.framework.common.enums.CommonStatusEnum;
import cn.fanstars.module.system.api.im.dto.ImNotifySendReqDTO;
import cn.fanstars.module.system.dal.dataobject.im.ImNotifyLogDO;
import cn.fanstars.module.system.dal.dataobject.im.ImTemplateDO;
import cn.fanstars.module.system.dal.dataobject.im.ImWebhookDO;
import cn.fanstars.module.system.framework.im.core.adapter.ImPlatformAdapter;
import cn.fanstars.module.system.framework.im.core.client.ImWebhookClient;
import cn.fanstars.module.system.framework.im.core.client.dto.ImWebhookSendResult;
import cn.fanstars.module.system.framework.im.core.enums.ImMsgTypeEnum;
import cn.fanstars.module.system.framework.im.core.factory.ImPlatformAdapterFactory;
import cn.fanstars.module.system.mq.message.im.ImNotifySendMessage;
import cn.fanstars.module.system.mq.producer.im.ImNotifyProducer;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.*;

/**
 * IM 通知发送 Service 实现类
 *
 * @author 繁星源码
 */
@Service
@Validated
@Slf4j
public class ImNotifySendServiceImpl implements ImNotifySendService {

    @Resource
    private ImTemplateService imTemplateService;
    @Resource
    private ImWebhookService imWebhookService;
    @Resource
    private ImNotifyLogService imNotifyLogService;
    @Resource
    private ImNotifyProducer imNotifyProducer;
    @Resource
    private ImPlatformAdapterFactory imPlatformAdapterFactory;
    @Resource
    private ImWebhookClient imWebhookClient;

    @Override
    public Long sendImNotify(ImNotifySendReqDTO reqDTO) {
        ImTemplateDO template = validateImTemplate(reqDTO.getTemplateCode());
        if (CommonStatusEnum.isDisable(template.getStatus())) {
            log.info("[sendImNotify][模板({})已禁用，跳过发送]", reqDTO.getTemplateCode());
            throw exception(IM_TEMPLATE_DISABLED);
        }
        validateTemplateParams(template, reqDTO.getTemplateParams());
        String renderedContent = imTemplateService.formatImTemplateContent(template.getContent(),
                reqDTO.getTemplateParams());

        List<ImWebhookDO> webhooks = resolveWebhooks(template, reqDTO.getWebhookIds());
        if (CollUtil.isEmpty(webhooks)) {
            throw exception(IM_WEBHOOK_NOT_BOUND);
        }

        Long firstLogId = null;
        for (ImWebhookDO webhook : webhooks) {
            boolean isSend = CommonStatusEnum.isEnable(webhook.getStatus());
            Long logId = imNotifyLogService.createImNotifyLog(template, webhook, renderedContent,
                    reqDTO.getTemplateParams(), isSend);
            if (firstLogId == null) {
                firstLogId = logId;
            }
            if (isSend) {
                imNotifyProducer.sendImNotifySendMessage(logId, webhook.getId(), template.getMsgType());
            }
        }
        return firstLogId;
    }

    @Override
    public void doSendImNotify(ImNotifySendMessage message) {
        ImNotifyLogDO notifyLog = imNotifyLogService.getImNotifyLog(message.getLogId());
        if (notifyLog == null) {
            log.warn("[doSendImNotify][日志({})不存在]", message.getLogId());
            return;
        }
        ImWebhookDO webhook = imWebhookService.getImWebhook(message.getWebhookId());
        if (webhook == null) {
            imNotifyLogService.updateImNotifySendResult(message.getLogId(), false, "NOT_FOUND", "Webhook 不存在");
            return;
        }
        ImPlatformAdapter adapter = imPlatformAdapterFactory.getAdapter(webhook.getPlatform());
        if (adapter == null) {
            imNotifyLogService.updateImNotifySendResult(message.getLogId(), false, "NO_ADAPTER", "平台适配器不存在");
            return;
        }
        ImMsgTypeEnum msgType = ImMsgTypeEnum.valueOfType(message.getMsgType());
        if (msgType == null) {
            msgType = ImMsgTypeEnum.TEXT;
        }
        try {
            String url = adapter.buildRequestUrl(webhook);
            String payload = adapter.buildPayload(webhook, notifyLog.getTemplateContent(), msgType);
            ImWebhookSendResult result = imWebhookClient.send(url, payload);
            imNotifyLogService.updateImNotifySendResult(message.getLogId(), result.isSuccess(),
                    result.getApiCode(), result.getApiMsg());
        } catch (Exception ex) {
            log.error("[doSendImNotify][发送异常，日志编号({})]", message.getLogId(), ex);
            imNotifyLogService.updateImNotifySendResult(message.getLogId(), false,
                    "EXCEPTION", ex.getMessage());
        }
    }

    @VisibleForTesting
    ImTemplateDO validateImTemplate(String templateCode) {
        ImTemplateDO template = imTemplateService.getImTemplateByCodeFromCache(templateCode);
        if (template == null) {
            throw exception(IM_TEMPLATE_NOT_EXISTS);
        }
        return template;
    }

    @VisibleForTesting
    void validateTemplateParams(ImTemplateDO template, Map<String, Object> templateParams) {
        if (CollUtil.isEmpty(template.getParams())) {
            return;
        }
        template.getParams().forEach(key -> {
            Object value = templateParams != null ? templateParams.get(key) : null;
            if (value == null) {
                throw exception(IM_TEMPLATE_PARAM_MISS, key);
            }
        });
    }

    private List<ImWebhookDO> resolveWebhooks(ImTemplateDO template, List<Long> webhookIds) {
        List<Long> targetIds;
        if (CollUtil.isNotEmpty(webhookIds)) {
            targetIds = webhookIds;
        } else {
            targetIds = imTemplateService.getWebhookIdsByTemplateId(template.getId());
        }
        if (CollUtil.isEmpty(targetIds)) {
            return List.of();
        }
        List<ImWebhookDO> webhooks = new ArrayList<>(imWebhookService.getImWebhookList(targetIds));
        webhooks.removeIf(Objects::isNull);
        return webhooks;
    }

}
