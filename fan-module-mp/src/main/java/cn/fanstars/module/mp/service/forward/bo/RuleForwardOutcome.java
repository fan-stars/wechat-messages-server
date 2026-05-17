package cn.fanstars.module.mp.service.forward.bo;

import lombok.Builder;
import lombok.Data;

/**
 * 单条同步转发规则执行结果（供 {@link cn.fanstars.module.mp.service.message.MpMessageReplyOrchestrator} 汇总）
 */
@Data
@Builder
public class RuleForwardOutcome {

    /** 规则编号，对应 mp_message_forward_rule.id */
    private Long ruleId;
    /** 优先级（越大越高），编排器按此顺序采纳 replyXml */
    private Integer priority;
    /** 同优先级时的次序键（当前取 ruleId） */
    private Long ruleOrderId;

    /** 下游 HTTP 响应体原文（写入转发日志 response_body） */
    private String responseBody;
    /** HTTP 状态码，如 200 */
    private Integer httpStatus;
    /** 请求耗时（毫秒） */
    private Integer durationMs;
    /** 日志状态：成功 / 失败 / 超时 / 跳过 */
    private Integer logStatus;
    /** 失败或超时时的错误摘要 */
    private String errorMsg;

    /**
     * 是否可作为微信被动回复候选
     * <p>
     * 须规则开启 {@code use_response_as_reply} 且 HTTP 成功、响应体非空
     */
    private boolean replyCandidate;

    /**
     * 候选被动回复 XML（明文或下游已 AES 包装）
     * <p>
     * 仅当 replyCandidate=true 时有值，由 Controller 决定是否再 encrypt
     */
    private String replyXml;

}
