package cn.fanstars.module.system.framework.sms.rest.service;

import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.api.request.QiniuSmsTemplateSaveReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.QiniuSmsTemplateRespVO;

/**
 * 七牛云短信模板审核 web-rest 门面
 */
public interface QiniuSmsRestService {

    SmsChannelProperties getChannelProperties();

    QiniuSmsTemplateRespVO createTemplate(QiniuSmsTemplateSaveReqVO reqVO);

    QiniuSmsTemplateRespVO updateTemplate(String templateId, QiniuSmsTemplateSaveReqVO reqVO);

}
