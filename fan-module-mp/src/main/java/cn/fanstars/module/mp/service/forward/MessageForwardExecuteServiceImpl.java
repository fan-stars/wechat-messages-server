package cn.fanstars.module.mp.service.forward;

import cn.fanstars.module.mp.controller.admin.open.vo.MpOpenHandleMessageReqVO;
import cn.fanstars.module.mp.dal.dataobject.account.MpAccountDO;
import cn.fanstars.module.mp.dal.dataobject.forward.log.MessageForwardLogDO;
import cn.fanstars.module.mp.dal.dataobject.forward.rule.MessageForwardRuleDO;
import cn.fanstars.module.mp.dal.mysql.forward.rule.MessageForwardRuleMapper;
import cn.fanstars.module.mp.enums.forward.MessageForwardLogStatusEnum;
import cn.fanstars.module.mp.enums.forward.MessageForwardModeEnum;
import cn.fanstars.module.mp.framework.retrofit2.bo.MpMessageForwardHttpResultBO;
import cn.fanstars.module.mp.framework.retrofit2.service.MpMessageForwardClient;
import cn.fanstars.module.mp.service.forward.bo.MessageForwardExecuteResultBO;
import cn.fanstars.module.mp.service.forward.log.MessageForwardLogService;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * 公众号消息转发执行 Service 实现类
 * <p>
 * 流程：按账号加载启用规则 → 匹配消息类型 → 同步/异步 HTTP 透传 → 可选采纳下游 XML 作为被动回复。
 */
@Service
@Slf4j
public class MessageForwardExecuteServiceImpl implements MessageForwardExecuteService {

    private static final String CONTENT_TYPE_XML = "application/xml; charset=UTF-8";
    /**
     * 便于下游关联转发规则
     */
    private static final String HEADER_RULE_ID = "X-Mp-Rule-Id";
    /**
     * 便于下游关联已入库消息
     */
    private static final String HEADER_MESSAGE_ID = "X-Mp-Message-Id";
    private static final int ERROR_MSG_MAX_LENGTH = 1000;

    @Resource
    private MessageForwardRuleMapper messageForwardRuleMapper;
    @Resource
    private MessageForwardLogService messageForwardLogService;
    @Resource
    private MessageForwardAsyncExecutor messageForwardAsyncExecutor;
    @Resource
    private MpMessageForwardClient mpMessageForwardClient;

    @Override
    public MessageForwardExecuteResultBO execute(MpAccountDO account, WxMpXmlMessage inMessage,
                                                 String rawContent, MpOpenHandleMessageReqVO reqVO,
                                                 Long messageId) {
        List<MessageForwardRuleDO> rules = messageForwardRuleMapper.selectEnabledListByAccountId(account.getId());
        if (rules.isEmpty()) {
            return MessageForwardExecuteResultBO.empty();
        }

        // 同步规则中，仅采纳第一条「使用响应作为回复」且成功的下游 XML
        String adoptedReplyXml = null;
        for (MessageForwardRuleDO rule : rules) {
            if (!matchMessageType(rule, inMessage)) {
                continue;
            }
            if (Objects.equals(rule.getForwardMode(), MessageForwardModeEnum.ASYNC.getMode())) {
                // 异步：不阻塞微信回调，不参与被动回复
                messageForwardAsyncExecutor.executeAsync(account, inMessage, rawContent, reqVO, messageId, rule);
                continue;
            }
            ForwardHttpResult httpResult = doForwardHttp(rule, rawContent, reqVO, messageId);
            // 已有回复时，后续规则的响应仅记日志，不再覆盖
            boolean skipReply = adoptedReplyXml != null
                    && Boolean.TRUE.equals(rule.getUseResponseAsReply())
                    && StrUtil.isNotBlank(httpResult.responseBody);
            Integer logStatus = httpResult.logStatus;
            if (skipReply) {
                logStatus = MessageForwardLogStatusEnum.SKIPPED.getStatus();
            } else if (adoptedReplyXml == null
                    && Boolean.TRUE.equals(rule.getUseResponseAsReply())
                    && StrUtil.isNotBlank(httpResult.responseBody)
                    && Objects.equals(logStatus, MessageForwardLogStatusEnum.SUCCESS.getStatus())) {
                adoptedReplyXml = httpResult.responseBody;
            }
            saveLogIfEnabled(rule, account, inMessage, messageId, rawContent, httpResult, logStatus);
        }
        return StrUtil.isNotBlank(adoptedReplyXml)
                ? MessageForwardExecuteResultBO.ofReply(adoptedReplyXml)
                : MessageForwardExecuteResultBO.empty();
    }

    /**
     * 执行单条规则（供 {@link MessageForwardAsyncExecutor} 异步线程调用）
     */
    public void executeForwardRule(MpAccountDO account, WxMpXmlMessage inMessage, String rawContent,
                                   MpOpenHandleMessageReqVO reqVO, Long messageId, MessageForwardRuleDO rule) {
        ForwardHttpResult httpResult = doForwardHttp(rule, rawContent, reqVO, messageId);
        saveLogIfEnabled(rule, account, inMessage, messageId, rawContent, httpResult, httpResult.logStatus);
    }

    /**
     * 匹配规则配置的消息类型；空表示全部类型
     */
    private boolean matchMessageType(MessageForwardRuleDO rule, WxMpXmlMessage inMessage) {
        if (StrUtil.isBlank(rule.getMessageTypes())) {
            return true;
        }
        Set<String> types = new HashSet<>(StrUtil.splitTrim(rule.getMessageTypes(), ','));
        if (WxConsts.XmlMsgType.EVENT.equals(inMessage.getMsgType())) {
            // 事件消息需同时匹配 event 子类型（如 subscribe）
            return types.contains(inMessage.getMsgType()) || types.contains(inMessage.getEvent());
        }
        return types.contains(inMessage.getMsgType());
    }

    /**
     * 单条规则 HTTP 透传，并转换为业务层结果（含日志状态）
     */
    private ForwardHttpResult doForwardHttp(MessageForwardRuleDO rule, String rawContent,
                                            MpOpenHandleMessageReqVO reqVO, Long messageId) {
        // 拼接微信验签 query，与 MpOpenController 收到的一致
        String url = buildTargetUrl(rule.getTargetUrl(), reqVO);
        int timeoutMs = rule.getTimeoutMs() != null ? rule.getTimeoutMs() : 3000;
        Map<String, String> headers = buildForwardHeaders(rule.getId(), messageId);
        MpMessageForwardHttpResultBO httpResult = mpMessageForwardClient.forward(url, headers, rawContent, timeoutMs);

        String responseBody = httpResult.getResponseBody();
        if (!Boolean.TRUE.equals(rule.getReceiveResponse())) {
            responseBody = null; // 不接收下游响应体，减少内存与日志体积
        }
        Integer httpStatus = httpResult.getHttpStatus();
        Integer logStatus;
        if (StrUtil.isNotBlank(httpResult.getErrorMsg())) {
            logStatus = isTimeoutError(httpResult.getErrorMsg())
                    ? MessageForwardLogStatusEnum.TIMEOUT.getStatus()
                    : MessageForwardLogStatusEnum.FAILURE.getStatus();
        } else {
            logStatus = httpStatus != null && httpStatus >= 200 && httpStatus < 300
                    ? MessageForwardLogStatusEnum.SUCCESS.getStatus()
                    : MessageForwardLogStatusEnum.FAILURE.getStatus();
        }
        return new ForwardHttpResult(responseBody, httpStatus, httpResult.getDurationMs(), logStatus,
                truncateErrorMsg(httpResult.getErrorMsg()));
    }

    private static Map<String, String> buildForwardHeaders(Long ruleId, Long messageId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", CONTENT_TYPE_XML);
        headers.put(HEADER_RULE_ID, String.valueOf(ruleId));
        if (messageId != null) {
            headers.put(HEADER_MESSAGE_ID, String.valueOf(messageId));
        }
        return headers;
    }

    private static boolean isTimeoutError(String errorMsg) {
        return StrUtil.containsIgnoreCase(errorMsg, "timeout")
                || StrUtil.containsIgnoreCase(errorMsg, "timed out");
    }

    /**
     * 将微信回调 query 原样附加到目标 URL，供下游自行验签
     */
    private static String buildTargetUrl(String targetUrl, MpOpenHandleMessageReqVO reqVO) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("signature", reqVO.getSignature())
                .queryParam("timestamp", reqVO.getTimestamp())
                .queryParam("nonce", reqVO.getNonce())
                .queryParam("openid", reqVO.getOpenid());
        if (StrUtil.isNotBlank(reqVO.getEncrypt_type())) {
            builder.queryParam("encrypt_type", reqVO.getEncrypt_type());
        }
        if (StrUtil.isNotBlank(reqVO.getMsg_signature())) {
            builder.queryParam("msg_signature", reqVO.getMsg_signature());
        }
        return builder.build(true).toUriString(); // encode=false，避免二次编码
    }

    /**
     * enable_log=0 时仍转发，但不落库 mp_message_forward_log
     */
    private void saveLogIfEnabled(MessageForwardRuleDO rule, MpAccountDO account, WxMpXmlMessage inMessage,
                                  Long messageId, String requestBody, ForwardHttpResult httpResult,
                                  Integer logStatus) {
        if (!Boolean.TRUE.equals(rule.getEnableLog())) {
            return;
        }
        MessageForwardLogDO logDO = MessageForwardLogDO.builder()
                .ruleId(rule.getId())
                .messageId(messageId)
                .accountId(account.getId())
                .appId(account.getAppId())
                .openid(inMessage.getFromUser())
                .forwardMode(rule.getForwardMode())
                .receiveResponse(rule.getReceiveResponse())
                .useResponseAsReply(rule.getUseResponseAsReply())
                .targetUrl(rule.getTargetUrl())
                .requestBody(requestBody)
                .responseBody(httpResult.responseBody)
                .httpStatus(httpResult.httpStatus)
                .status(logStatus)
                .durationMs(httpResult.durationMs)
                .errorMsg(httpResult.errorMsg)
                .build();
        messageForwardLogService.createMessageForwardLog(logDO);
    }

    private static String truncateErrorMsg(String errorMsg) {
        if (StrUtil.isBlank(errorMsg)) {
            return null;
        }
        return StrUtil.maxLength(errorMsg, ERROR_MSG_MAX_LENGTH);
    }

    /**
     * 单条规则转发结果（业务层，含日志状态）
     */
    private static final class ForwardHttpResult {
        private final String responseBody;
        private final Integer httpStatus;
        private final Integer durationMs;
        private final Integer logStatus;
        private final String errorMsg;

        private ForwardHttpResult(String responseBody, Integer httpStatus, Integer durationMs,
                                  Integer logStatus, String errorMsg) {
            this.responseBody = responseBody;
            this.httpStatus = httpStatus;
            this.durationMs = durationMs;
            this.logStatus = logStatus;
            this.errorMsg = errorMsg;
        }
    }

}
