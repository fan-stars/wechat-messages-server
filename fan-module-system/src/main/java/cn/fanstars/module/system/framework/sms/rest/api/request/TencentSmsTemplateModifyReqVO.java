package cn.fanstars.module.system.framework.sms.rest.api.request;

import lombok.Data;

import java.util.TreeMap;

/**
 * 腾讯云 ModifySmsTemplate 请求体
 */
@Data
public class TencentSmsTemplateModifyReqVO {

    private Long templateId;

    private String templateName;

    private String templateContent;

    private Long smsType;

    private Long international = 0L;

    private String remark;

    public TreeMap<String, Object> toBody() {
        TreeMap<String, Object> body = new TreeMap<>();
        body.put("TemplateId", templateId);
        body.put("TemplateName", templateName);
        body.put("TemplateContent", templateContent);
        body.put("SmsType", smsType);
        body.put("International", international);
        body.put("Remark", remark);
        return body;
    }

}
