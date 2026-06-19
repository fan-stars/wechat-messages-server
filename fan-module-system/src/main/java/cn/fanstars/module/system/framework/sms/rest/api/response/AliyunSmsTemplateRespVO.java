package cn.fanstars.module.system.framework.sms.rest.api.response;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * 阿里云短信模板审核响应（AddSmsTemplate / ModifySmsTemplate）
 */
@Data
public class AliyunSmsTemplateRespVO {

    private static final String RESPONSE_CODE_SUCCESS = "OK";

    private String code;

    private String message;

    private String templateCode;

    private String templateContent;

    private Integer templateStatus;

    private String reason;

    public boolean isSuccess() {
        return RESPONSE_CODE_SUCCESS.equals(code);
    }

    /**
     * 从原始 JSON 响应解析
     */
    public static AliyunSmsTemplateRespVO fromJson(String body) {
        JSONObject json = JSONUtil.parseObj(body);
        AliyunSmsTemplateRespVO vo = new AliyunSmsTemplateRespVO();
        vo.setCode(json.getStr("Code"));
        vo.setMessage(json.getStr("Message"));
        vo.setTemplateCode(json.getStr("TemplateCode"));
        vo.setTemplateContent(json.getStr("TemplateContent"));
        vo.setTemplateStatus(json.getInt("TemplateStatus"));
        vo.setReason(json.getStr("Reason"));
        return vo;
    }

}
