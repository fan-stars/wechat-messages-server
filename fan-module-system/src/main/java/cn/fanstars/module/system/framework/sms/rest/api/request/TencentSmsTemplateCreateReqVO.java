package cn.fanstars.module.system.framework.sms.rest.api.request;

import lombok.Data;

import java.util.TreeMap;

/**
 * 腾讯云 AddSmsTemplate 请求体
 */
@Data
public class TencentSmsTemplateCreateReqVO {

    private String templateName;

    private String templateContent;

    /**
     * 0 普通短信 1 营销短信
     */
    private Long smsType;

    /**
     * 0 国内短信
     */
    private Long international = 0L;

    private String remark;

    public TreeMap<String, Object> toBody() {
        TreeMap<String, Object> body = new TreeMap<>();
        body.put("TemplateName", templateName);
        body.put("TemplateContent", templateContent);
        body.put("SmsType", smsType);
        body.put("International", international);
        body.put("Remark", remark);
        return body;
    }

}
