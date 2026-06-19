package cn.fanstars.module.system.framework.sms.rest.api.request;

import lombok.Data;

import java.util.TreeMap;

/**
 * 阿里云 AddSmsTemplate 请求参数
 */
@Data
public class AliyunSmsTemplateCreateReqVO {

    /**
     * 模板类型：0 验证码 1 短信通知 2 推广短信
     */
    private Integer templateType;

    private String templateName;

    private String templateContent;

    private String remark;

    /**
     * 转为 RPC 查询参数（TreeMap 保证签名顺序）
     */
    public TreeMap<String, Object> toQueryParams() {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("TemplateType", templateType);
        params.put("TemplateName", templateName);
        params.put("TemplateContent", templateContent);
        if (remark != null && !remark.isBlank()) {
            params.put("Remark", remark);
        }
        return params;
    }

}
