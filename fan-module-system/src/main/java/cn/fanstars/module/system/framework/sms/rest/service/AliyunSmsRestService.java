package cn.fanstars.module.system.framework.sms.rest.service;

import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.api.request.AliyunSmsTemplateCreateReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.request.AliyunSmsTemplateModifyReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.AliyunSmsTemplateRespVO;

/**
 * 阿里云短信模板审核 web-rest 门面
 */
public interface AliyunSmsRestService {

    /**
     * 渠道配置（与工厂缓存实例绑定）
     */
    SmsChannelProperties getChannelProperties();

    /**
     * 提交模板审核（AddSmsTemplate）
     */
    AliyunSmsTemplateRespVO addSmsTemplate(AliyunSmsTemplateCreateReqVO reqVO);

    /**
     * 修改模板并重新审核（ModifySmsTemplate）
     */
    AliyunSmsTemplateRespVO modifySmsTemplate(AliyunSmsTemplateModifyReqVO reqVO);

}
