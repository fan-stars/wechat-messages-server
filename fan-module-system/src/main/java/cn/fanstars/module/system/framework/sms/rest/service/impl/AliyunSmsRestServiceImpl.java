package cn.fanstars.module.system.framework.sms.rest.service.impl;

import cn.hutool.json.JSONUtil;
import cn.fanstars.framework.rest.core.HttpServiceFactory;
import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.api.AliyunSmsTemplateApi;
import cn.fanstars.module.system.framework.sms.rest.api.request.AliyunSmsTemplateCreateReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.request.AliyunSmsTemplateModifyReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.AliyunSmsTemplateRespVO;
import cn.fanstars.module.system.framework.sms.rest.interceptor.AliyunSmsAuthInterceptor;
import cn.fanstars.module.system.framework.sms.rest.interceptor.AliyunSmsRpcContext;
import cn.fanstars.module.system.framework.sms.rest.service.AliyunSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.support.SmsRestClientSupport;
import lombok.Getter;

/**
 * 阿里云短信模板审核 web-rest 门面实现
 * <p>
 * 构造时创建并缓存 {@link AliyunSmsTemplateApi} 代理，避免每次请求重复 build RestClient。
 */
public class AliyunSmsRestServiceImpl implements AliyunSmsRestService {

    private static final String BASE_URL = "https://dysmsapi.aliyuncs.com/";
    private static final String ACTION_ADD = "AddSmsTemplate";
    private static final String ACTION_MODIFY = "ModifySmsTemplate";

    @Getter
    private final SmsChannelProperties channelProperties;

    private final AliyunSmsTemplateApi api;

    public AliyunSmsRestServiceImpl(SmsChannelProperties channelProperties, HttpServiceFactory httpServiceFactory) {
        this.channelProperties = channelProperties;
        this.api = SmsRestClientSupport.createClient(httpServiceFactory, BASE_URL,
                new AliyunSmsAuthInterceptor(channelProperties), AliyunSmsTemplateApi.class);
    }

    @Override
    public AliyunSmsTemplateRespVO addSmsTemplate(AliyunSmsTemplateCreateReqVO reqVO) {
        AliyunSmsRpcContext.setAction(ACTION_ADD);
        try {
            String body = api.addSmsTemplate(reqVO.toQueryParams());
            return AliyunSmsTemplateRespVO.fromJson(body);
        } finally {
            AliyunSmsRpcContext.clear();
        }
    }

    @Override
    public AliyunSmsTemplateRespVO modifySmsTemplate(AliyunSmsTemplateModifyReqVO reqVO) {
        AliyunSmsRpcContext.setAction(ACTION_MODIFY);
        try {
            String body = api.modifySmsTemplate(reqVO.toQueryParams());
            return AliyunSmsTemplateRespVO.fromJson(body);
        } finally {
            AliyunSmsRpcContext.clear();
        }
    }

}
