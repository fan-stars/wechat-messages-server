package cn.fanstars.module.mp.service.forward;

import cn.fanstars.module.mp.controller.admin.open.vo.MpOpenHandleMessageReqVO;
import cn.fanstars.module.mp.dal.dataobject.account.MpAccountDO;
import cn.fanstars.module.mp.dal.dataobject.forward.log.MessageForwardLogDO;
import cn.fanstars.module.mp.dal.dataobject.forward.rule.MessageForwardRuleDO;
import cn.fanstars.module.mp.dal.mysql.forward.rule.MessageForwardRuleMapper;
import cn.fanstars.module.mp.enums.forward.MessageForwardLogStatusEnum;
import cn.fanstars.module.mp.framework.mp.core.MpServiceFactory;
import cn.fanstars.module.mp.framework.retrofit2.bo.MpMessageForwardHttpResultBO;
import cn.fanstars.module.mp.framework.retrofit2.service.MpMessageForwardClient;
import cn.fanstars.module.mp.service.forward.bo.RuleForwardOutcome;
import cn.fanstars.module.mp.service.forward.log.MessageForwardLogService;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.util.crypto.WxMpCryptUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 公众号消息转发执行 Service 实现类
 * <p>
 * 供 {@link cn.fanstars.module.mp.service.message.MpMessageReplyOrchestrator} 调用：规则匹配、单条 HTTP 透传（Retrofit）、日志落库。
 * 同步规则 HTTP 本身不写日志，由编排器在合适的时机调用 {@link #saveSyncRuleLog}。
 */
@Service
@Slf4j
public class MessageForwardExecuteServiceImpl {

    /**
     * 与微信回调一致的请求 Content-Type
     */
    private static final String CONTENT_TYPE_XML = "application/xml; charset=UTF-8";
    /**
     * 透传请求头：转发规则编号
     */
    private static final String HEADER_RULE_ID = "X-Mp-Rule-Id";
    /**
     * 透传请求头：已入库消息编号
     */
    private static final String HEADER_MESSAGE_ID = "X-Mp-Message-Id";
    /**
     * 转发日志 error_msg 最大长度
     */
    private static final int ERROR_MSG_MAX_LENGTH = 1000;
    /**
     * 规则未配置 timeout_ms 时的默认 HTTP 超时（毫秒）
     */
    private static final int DEFAULT_RULE_TIMEOUT_MS = 3000;

    @Resource
    private MessageForwardRuleMapper messageForwardRuleMapper;
    @Resource
    private MessageForwardLogService messageForwardLogService;
    @Resource
    private MpMessageForwardClient mpMessageForwardClient;
    @Resource
    private MpServiceFactory mpServiceFactory;

    /**
     * 查询命中消息类型的启用规则
     *
     * @param accountId 公众号账号编号
     * @param inMessage 入站消息（用于 message_types 匹配）
     * @return 已按 priority DESC、id ASC 排序的规则列表
     */
    public List<MessageForwardRuleDO> getMatchedRules(Long accountId, WxMpXmlMessage inMessage) {
        return messageForwardRuleMapper.selectEnabledListByAccountId(accountId).stream()
                // message_types 为空表示全部类型
                .filter(rule -> matchMessageType(rule, inMessage))
                .sorted(Comparator.comparing(MessageForwardRuleDO::getPriority, Comparator.reverseOrder())
                        // 同优先级按 id 稳定排序
                        .thenComparing(MessageForwardRuleDO::getId))
                .collect(Collectors.toList());
    }

    /**
     * 单条同步规则 HTTP 透传（不写日志，供并行编排）
     * <p>
     * OkHttp 超时默认取规则 {@code timeout_ms}。仅当 {@code use_response_as_reply=true} 时，才与
     * {@code replyWaitRemainingMs} 取 min，避免占用被动回复窗口仍无法回微信。
     * <p>
     * 仅 {@code receive_response=true}、不作为被动回复时，编排器主线程超时返回后 HTTP 仍按规则完整超时继续等待，
     * 由 {@link cn.fanstars.module.mp.service.message.MpMessageReplyOrchestrator} 在 Future 完成时补写转发日志（含响应体）。
     *
     * @param replyWaitRemainingMs 编排器被动回复窗口剩余时间（毫秒）；仅收紧「响应作回复」规则的 HTTP 超时
     * @return 转发结果；{@link RuleForwardOutcome#isReplyCandidate()} 为 true 时可作为微信被动回复
     */
    public RuleForwardOutcome forwardSyncRuleHttp(String rawContent, MpOpenHandleMessageReqVO reqVO, Long messageId,
                                                  MessageForwardRuleDO rule, int replyWaitRemainingMs) {
        int timeoutMs = rule.getTimeoutMs() != null ? rule.getTimeoutMs() : DEFAULT_RULE_TIMEOUT_MS;
        if (Boolean.TRUE.equals(rule.getUseResponseAsReply()) && replyWaitRemainingMs > 0) {
            // 候选被动回复须在窗口内完成，HTTP 超时不超过剩余预算
            timeoutMs = Math.min(timeoutMs, replyWaitRemainingMs);
        }
        ForwardHttpResult httpResult = doForwardHttp(rule, rawContent, reqVO, messageId, timeoutMs);
        // 候选回复须：use_response_as_reply + receive_response + 2xx + 非空 body
        boolean replyCandidate = Boolean.TRUE.equals(rule.getUseResponseAsReply())
                && StrUtil.isNotBlank(httpResult.responseBody)
                && Objects.equals(httpResult.logStatus, MessageForwardLogStatusEnum.SUCCESS.getStatus());
        return RuleForwardOutcome.builder()
                .ruleId(rule.getId())
                .priority(rule.getPriority())
                .ruleOrderId(rule.getId())
                // 日志用，可能与 replyXml 相同
                .responseBody(httpResult.responseBody)
                .httpStatus(httpResult.httpStatus)
                .durationMs(httpResult.durationMs)
                .logStatus(httpResult.logStatus)
                .errorMsg(httpResult.errorMsg)
                .replyCandidate(replyCandidate)
                // 交给编排器 / Controller 作为被动回复候选
                .replyXml(replyCandidate ? httpResult.responseBody : null)
                .build();
    }

    /**
     * 写入单条同步规则转发日志
     *
     * @param logStatus 可为 {@link MessageForwardLogStatusEnum#SKIPPED}（低优先级已有回复时）
     */
    public void saveSyncRuleLog(MpAccountDO account, WxMpXmlMessage inMessage, MpOpenHandleMessageReqVO reqVO,
                                Long messageId, String rawContent,
                                MessageForwardRuleDO rule, RuleForwardOutcome outcome, Integer logStatus) {
        ForwardHttpResult httpResult = new ForwardHttpResult(outcome.getResponseBody(), outcome.getHttpStatus(),
                outcome.getDurationMs(), logStatus, outcome.getErrorMsg());
        saveLogIfEnabled(rule, account, inMessage, reqVO, messageId, rawContent, httpResult, logStatus);
    }

    /**
     * 按优先级从已完成的同步规则结果中选取被动回复
     *
     * @param syncRules  已按 priority DESC 排序
     * @param outcomeMap ruleId → 转发结果
     * @return 最高优先级候选的 replyXml；无候选时返回 null
     */
    public String pickForwardReplyByPriority(List<MessageForwardRuleDO> syncRules,
                                             Map<Long, RuleForwardOutcome> outcomeMap) {
        for (MessageForwardRuleDO rule : syncRules) {
            RuleForwardOutcome outcome = outcomeMap.get(rule.getId());
            if (outcome != null && outcome.isReplyCandidate()) {
                // 第一条候选即最高优先级
                return outcome.getReplyXml();
            }
        }
        return null;
    }

    /**
     * 执行单条异步规则：HTTP 透传并按配置写日志，不参与被动回复
     */
    public void executeForwardRule(MpAccountDO account, WxMpXmlMessage inMessage, String rawContent,
                                   MpOpenHandleMessageReqVO reqVO, Long messageId, MessageForwardRuleDO rule) {
        int timeoutMs = rule.getTimeoutMs() != null ? rule.getTimeoutMs() : DEFAULT_RULE_TIMEOUT_MS;
        ForwardHttpResult httpResult = doForwardHttp(rule, rawContent, reqVO, messageId, timeoutMs);
        // 异步规则：可按 receive_response 记录响应体，永不参与被动回复
        saveLogIfEnabled(rule, account, inMessage, reqVO, messageId, rawContent, httpResult, httpResult.logStatus);
    }

    /**
     * message_types 为空表示匹配全部；事件消息同时匹配 event 字段
     */
    private boolean matchMessageType(MessageForwardRuleDO rule, WxMpXmlMessage inMessage) {
        if (StrUtil.isBlank(rule.getMessageTypes())) {
            // 未配置则匹配所有类型
            return true;
        }
        Set<String> types = new HashSet<>(StrUtil.splitTrim(rule.getMessageTypes(), ','));
        if (WxConsts.XmlMsgType.EVENT.equals(inMessage.getMsgType())) {
            // 如 subscribe、CLICK
            return types.contains(inMessage.getMsgType()) || types.contains(inMessage.getEvent());
        }
        // text、image 等
        return types.contains(inMessage.getMsgType());
    }

    /**
     * 单条规则 HTTP 透传（Retrofit 同步调用）
     */
    private ForwardHttpResult doForwardHttp(MessageForwardRuleDO rule, String rawContent,
                                            MpOpenHandleMessageReqVO reqVO, Long messageId, int timeoutMs) {
        // 透传微信 query 参数，便于下游验签
        String url = buildTargetUrl(rule.getTargetUrl(), reqVO);
        Map<String, String> headers = buildForwardHeaders(rule.getId(), messageId);
        MpMessageForwardHttpResultBO httpResult = mpMessageForwardClient.forward(url, headers, rawContent, timeoutMs);

        String responseBody = httpResult.getResponseBody();
        if (!Boolean.TRUE.equals(rule.getReceiveResponse())) {
            // 不读响应体，也不作为被动回复
            responseBody = null;
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
     * 拼接与微信回调一致的 query，便于下游验签、解密
     */
    private static String buildTargetUrl(String targetUrl, MpOpenHandleMessageReqVO reqVO) {
        // 与微信回调 URL 参数一致，下游可用相同 token 验签
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("signature", reqVO.getSignature())
                .queryParam("timestamp", reqVO.getTimestamp())
                .queryParam("nonce", reqVO.getNonce())
                .queryParam("openid", reqVO.getOpenid());
        if (StrUtil.isNotBlank(reqVO.getEncrypt_type())) {
            builder.queryParam("encrypt_type", reqVO.getEncrypt_type());
        }
        if (StrUtil.isNotBlank(reqVO.getMsg_signature())) {
            // AES 模式消息体签名校验
            builder.queryParam("msg_signature", reqVO.getMsg_signature());
        }
        return builder.build(true).toUriString();
    }

    private void saveLogIfEnabled(MessageForwardRuleDO rule, MpAccountDO account, WxMpXmlMessage inMessage,
                                  MpOpenHandleMessageReqVO reqVO, Long messageId, String requestBody,
                                  ForwardHttpResult httpResult, Integer logStatus) {
        if (!Boolean.TRUE.equals(rule.getEnableLog())) {
            // 规则未开启日志则跳过持久化
            return;
        }
        String requestBodyPlaintext = decryptLogRequestBodyIfPossible(account, reqVO, requestBody);
        String responseBodyPlaintext = decryptLogResponseBodyIfPossible(account, httpResult.responseBody);
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
                .requestBody(requestBodyPlaintext)
                .responseBody(responseBodyPlaintext)
                .httpStatus(httpResult.httpStatus)
                .status(logStatus)
                .durationMs(httpResult.durationMs)
                .errorMsg(httpResult.errorMsg)
                .build();
        messageForwardLogService.createMessageForwardLog(logDO);
    }

    /**
     * 将日志中的请求体尽量解密为明文（仅 aes 且含 Encrypt 时）。
     *
     * 使用 weixin-java 的 {@link WxMpXmlMessage#fromEncryptedXml(String, WxMpConfigStorage, String, String, String)} 解密。
     */
    private String decryptLogRequestBodyIfPossible(MpAccountDO account, MpOpenHandleMessageReqVO reqVO, String requestBody) {
        if (StrUtil.isBlank(requestBody) || account == null || reqVO == null) {
            return requestBody;
        }
        if (!MpOpenHandleMessageReqVO.ENCRYPT_TYPE_AES.equals(reqVO.getEncrypt_type())) {
            return requestBody;
        }
        String trimmed = StrUtil.trim(requestBody);
        if (!StrUtil.contains(trimmed, "<Encrypt>")) {
            return requestBody;
        }
        try {
            WxMpConfigStorage configStorage = mpServiceFactory.getRequiredMpService(account.getAppId()).getWxMpConfigStorage();
            WxMpXmlMessage decrypted = WxMpXmlMessage.fromEncryptedXml(trimmed, configStorage,
                    reqVO.getTimestamp(), reqVO.getNonce(), reqVO.getMsg_signature());
            // WxMpXmlMessage 无统一 toXml()，toString() 为明文字段串（满足“已解密”诉求）
            return String.valueOf(decrypted);
        } catch (Exception ex) {
            return requestBody;
        }
    }

    /**
     * 将日志中的响应体尽量解密为明文（仅含 Encrypt 且带签名元数据时）。
     *
     * 使用 weixin-java 的 {@link WxMpCryptUtil#decryptXml(String, String, String, String)} 解密。
     */
    private String decryptLogResponseBodyIfPossible(MpAccountDO account, String responseBody) {
        if (StrUtil.isBlank(responseBody) || account == null) {
            return responseBody;
        }
        String trimmed = StrUtil.trim(responseBody);
        if (!StrUtil.contains(trimmed, "<Encrypt>")) {
            return responseBody;
        }
        try {
            WxMpConfigStorage configStorage = mpServiceFactory.getRequiredMpService(account.getAppId()).getWxMpConfigStorage();
            WxMpCryptUtil cryptUtil = new WxMpCryptUtil(configStorage);
            // 响应体为“已包装的加密被动回复”，按微信协议包含 MsgSignature/TimeStamp/Nonce
            String msgSignature = extractXmlTagValue(trimmed, "MsgSignature");
            String timeStamp = extractXmlTagValue(trimmed, "TimeStamp");
            String nonce = extractXmlTagValue(trimmed, "Nonce");
            if (StrUtil.isBlank(msgSignature) || StrUtil.isBlank(timeStamp) || StrUtil.isBlank(nonce)) {
                return responseBody;
            }
            return cryptUtil.decryptXml(msgSignature, timeStamp, nonce, trimmed);
        } catch (Exception ex) {
            return responseBody;
        }
    }

    private static String extractXmlTagValue(String xml, String tag) {
        if (StrUtil.isBlank(xml) || StrUtil.isBlank(tag)) {
            return null;
        }
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int start = StrUtil.indexOfIgnoreCase(xml, open, 0);
        if (start < 0) {
            return null;
        }
        int end = StrUtil.indexOfIgnoreCase(xml, close, start + open.length());
        if (end < 0) {
            return null;
        }
        String value = xml.substring(start + open.length(), end);
        value = StrUtil.removePrefix(value, "<![CDATA[");
        value = StrUtil.removeSuffix(value, "]]>");
        return StrUtil.trim(value);
    }

    private static String truncateErrorMsg(String errorMsg) {
        if (StrUtil.isBlank(errorMsg)) {
            return null;
        }
        return StrUtil.maxLength(errorMsg, ERROR_MSG_MAX_LENGTH);
    }

    /**
     * 单条规则 HTTP 透传的中间结果（尚未写入转发日志）
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
