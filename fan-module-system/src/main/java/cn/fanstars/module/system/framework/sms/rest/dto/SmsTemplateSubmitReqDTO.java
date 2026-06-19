package cn.fanstars.module.system.framework.sms.rest.dto;

import lombok.Data;

/**
 * 短信模板提交审核请求 DTO
 */
@Data
public class SmsTemplateSubmitReqDTO {

    /**
     * 本地模板编码
     */
    private String code;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板内容
     */
    private String content;
    /**
     * 模板类型，参见 {@link cn.fanstars.module.system.enums.sms.SmsTemplateTypeEnum}
     */
    private Integer type;
    /**
     * 备注
     */
    private String remark;

}
