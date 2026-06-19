package cn.fanstars.module.system.framework.sms.rest.service;

import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.api.request.TencentSmsTemplateCreateReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.request.TencentSmsTemplateModifyReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.TencentSmsTemplateRespVO;

/**
 * 腾讯云短信模板审核 web-rest 门面
 */
public interface TencentSmsRestService {

    SmsChannelProperties getChannelProperties();

    TencentSmsTemplateRespVO addSmsTemplate(TencentSmsTemplateCreateReqVO reqVO);

    TencentSmsTemplateRespVO modifySmsTemplate(TencentSmsTemplateModifyReqVO reqVO);

}
