package cn.fanstars.module.system.framework.sms.rest.service.impl;

import cn.fanstars.framework.rest.core.HttpServiceFactory;
import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.api.QiniuSmsTemplateApi;
import cn.fanstars.module.system.framework.sms.rest.api.request.QiniuSmsTemplateSaveReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.QiniuSmsTemplateRespVO;
import cn.fanstars.module.system.framework.sms.rest.interceptor.QiniuSmsAuthInterceptor;
import cn.fanstars.module.system.framework.sms.rest.service.QiniuSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.support.SmsRestClientSupport;
import lombok.Getter;

/**
 * 七牛云短信模板审核 web-rest 门面实现
 */
public class QiniuSmsRestServiceImpl implements QiniuSmsRestService {

    private static final String BASE_URL = "https://sms.qiniuapi.com/";

    @Getter
    private final SmsChannelProperties channelProperties;

    private final QiniuSmsTemplateApi api;

    public QiniuSmsRestServiceImpl(SmsChannelProperties channelProperties, HttpServiceFactory httpServiceFactory) {
        this.channelProperties = channelProperties;
        this.api = SmsRestClientSupport.createClient(httpServiceFactory, BASE_URL,
                new QiniuSmsAuthInterceptor(channelProperties), QiniuSmsTemplateApi.class);
    }

    @Override
    public QiniuSmsTemplateRespVO createTemplate(QiniuSmsTemplateSaveReqVO reqVO) {
        return QiniuSmsTemplateRespVO.fromJson(api.createTemplate(reqVO));
    }

    @Override
    public QiniuSmsTemplateRespVO updateTemplate(String templateId, QiniuSmsTemplateSaveReqVO reqVO) {
        return QiniuSmsTemplateRespVO.fromJson(api.updateTemplate(templateId, reqVO));
    }

}
