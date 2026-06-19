package cn.fanstars.module.system.framework.sms.rest.api.request;

import lombok.Data;

/**
 * 赛邮短信模板创建/更新请求
 */
@Data
public class SubmailSmsTemplateSaveReqVO {

    private String title;

    private String content;

    private String signature;

    private String templateId;

}
