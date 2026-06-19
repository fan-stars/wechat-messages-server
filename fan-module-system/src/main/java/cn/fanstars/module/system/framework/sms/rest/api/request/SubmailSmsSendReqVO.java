package cn.fanstars.module.system.framework.sms.rest.api.request;

import lombok.Data;

import java.util.Map;

/**
 * 赛邮短信发送请求
 */
@Data
public class SubmailSmsSendReqVO {

    private String to;

    /** 云平台模板 ID（xsend 的 project） */
    private String templateId;

    private String sign;

    /** 无模板 ID 时使用的正文（含 {@code @var(key)}） */
    private String content;

    private Map<String, String> vars;

}
