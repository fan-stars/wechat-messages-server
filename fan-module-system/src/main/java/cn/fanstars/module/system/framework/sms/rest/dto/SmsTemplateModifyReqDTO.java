package cn.fanstars.module.system.framework.sms.rest.dto;

import lombok.Data;

/**
 * 短信模板修改并重新审核请求 DTO
 */
@Data
public class SmsTemplateModifyReqDTO {

    /**
     * 短信 API 的模板编号
     */
    private String apiTemplateId;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板内容
     */
    private String content;
    /**
     * 模板类型
     */
    private Integer type;
    /**
     * 备注
     */
    private String remark;

}
