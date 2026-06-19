package cn.fanstars.module.system.framework.sms.rest.service;

import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.api.request.SubmailSmsSendReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.request.SubmailSmsTemplateSaveReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.SubmailSmsRespVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.SubmailSmsTemplateQueryRespVO;

/**
 * 赛邮短信 web-rest 门面
 */
public interface SubmailSmsRestService {

    SmsChannelProperties getChannelProperties();

    SubmailSmsTemplateQueryRespVO getTemplate(String templateId);

    SubmailSmsRespVO createTemplate(SubmailSmsTemplateSaveReqVO reqVO);

    SubmailSmsRespVO updateTemplate(SubmailSmsTemplateSaveReqVO reqVO);

    SubmailSmsRespVO sendSms(SubmailSmsSendReqVO reqVO);

}
