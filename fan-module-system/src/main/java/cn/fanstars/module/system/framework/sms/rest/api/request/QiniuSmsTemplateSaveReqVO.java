package cn.fanstars.module.system.framework.sms.rest.api.request;

import lombok.Data;

/**
 * 七牛云短信模板创建/更新请求体
 */
@Data
public class QiniuSmsTemplateSaveReqVO {

    private String name;

    private String template;

    /**
     * verification / notification / marketing
     */
    private String type;

    private String signature;

}
