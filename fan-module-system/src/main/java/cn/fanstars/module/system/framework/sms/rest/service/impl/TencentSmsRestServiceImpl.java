package cn.fanstars.module.system.framework.sms.rest.service.impl;

import cn.hutool.json.JSONUtil;
import cn.fanstars.framework.rest.core.HttpServiceFactory;
import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.api.TencentSmsTemplateApi;
import cn.fanstars.module.system.framework.sms.rest.api.request.TencentSmsTemplateCreateReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.request.TencentSmsTemplateModifyReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.TencentSmsTemplateRespVO;
import cn.fanstars.module.system.framework.sms.rest.interceptor.TencentSmsAuthInterceptor;
import cn.fanstars.module.system.framework.sms.rest.interceptor.TencentSmsRpcContext;
import cn.fanstars.module.system.framework.sms.rest.service.TencentSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.support.SmsRestClientSupport;
import lombok.Getter;

/**
 * 腾讯云短信模板审核 web-rest 门面实现
 */
public class TencentSmsRestServiceImpl implements TencentSmsRestService {

    private static final String BASE_URL = "https://sms.tencentcloudapi.com/";
    private static final String ACTION_ADD = "AddSmsTemplate";
    private static final String ACTION_MODIFY = "ModifySmsTemplate";

    @Getter
    private final SmsChannelProperties channelProperties;

    private final TencentSmsTemplateApi api;

    public TencentSmsRestServiceImpl(SmsChannelProperties channelProperties, HttpServiceFactory httpServiceFactory) {
        this.channelProperties = channelProperties;
        this.api = SmsRestClientSupport.createClient(httpServiceFactory, BASE_URL,
                new TencentSmsAuthInterceptor(channelProperties), TencentSmsTemplateApi.class);
    }

    @Override
    public TencentSmsTemplateRespVO addSmsTemplate(TencentSmsTemplateCreateReqVO reqVO) {
        TencentSmsRpcContext.setAction(ACTION_ADD);
        try {
            String body = api.addSmsTemplate(JSONUtil.toJsonStr(reqVO.toBody()));
            return TencentSmsTemplateRespVO.fromJson(body);
        } finally {
            TencentSmsRpcContext.clear();
        }
    }

    @Override
    public TencentSmsTemplateRespVO modifySmsTemplate(TencentSmsTemplateModifyReqVO reqVO) {
        TencentSmsRpcContext.setAction(ACTION_MODIFY);
        try {
            String body = api.modifySmsTemplate(JSONUtil.toJsonStr(reqVO.toBody()));
            return TencentSmsTemplateRespVO.fromJson(body);
        } finally {
            TencentSmsRpcContext.clear();
        }
    }

}
