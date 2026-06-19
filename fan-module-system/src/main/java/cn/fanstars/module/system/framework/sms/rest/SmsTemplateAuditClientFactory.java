package cn.fanstars.module.system.framework.sms.rest;

import cn.fanstars.module.system.dal.dataobject.sms.SmsChannelDO;
import cn.fanstars.module.system.framework.sms.core.client.SmsClient;
import cn.fanstars.module.system.framework.sms.core.enums.SmsChannelEnum;
import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.aliyun.AliyunSmsTemplateAuditClient;
import cn.fanstars.module.system.framework.sms.rest.factory.SmsRestServiceFactory;
import cn.fanstars.module.system.framework.sms.rest.local.LocalSmsTemplateAuditClient;
import cn.fanstars.module.system.framework.sms.rest.qiniu.QiniuSmsTemplateAuditClient;
import cn.fanstars.module.system.framework.sms.rest.submail.SubmailSmsTemplateAuditClient;
import cn.fanstars.module.system.framework.sms.rest.tencent.TencentSmsTemplateAuditClient;
import cn.fanstars.module.system.service.sms.SmsChannelService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 短信模板审核客户端工厂
 */
@Component
public class SmsTemplateAuditClientFactory {

    @Resource
    private SmsChannelService smsChannelService;
    @Resource
    private SmsRestServiceFactory smsRestServiceFactory;

    public SmsTemplateAuditClient getClient(Long channelId) {
        SmsChannelDO channel = smsChannelService.getSmsChannel(channelId);
        SmsClient smsClient = smsChannelService.getSmsClient(channelId);
        return getClient(channel, smsClient);
    }

    public SmsTemplateAuditClient getClient(SmsChannelDO channel, SmsClient smsClient) {
        SmsChannelProperties properties = buildProperties(channel);
        SmsChannelEnum channelEnum = SmsChannelEnum.getByCode(channel.getCode());
        if (channelEnum == null) {
            return new LocalSmsTemplateAuditClient(smsClient, channel.getCode());
        }
        return switch (channelEnum) {
            case ALIYUN -> new AliyunSmsTemplateAuditClient(smsClient,
                    smsRestServiceFactory.getAliyunRestService(properties));
            case TENCENT -> new TencentSmsTemplateAuditClient(smsClient,
                    smsRestServiceFactory.getTencentRestService(properties));
            case QINIU -> new QiniuSmsTemplateAuditClient(smsClient,
                    smsRestServiceFactory.getQiniuRestService(properties));
            case SUBMAIL -> new SubmailSmsTemplateAuditClient(smsClient,
                    smsRestServiceFactory.getSubmailRestService(properties));
            case HUAWEI, DEBUG_DING_TALK -> new LocalSmsTemplateAuditClient(smsClient, channel.getCode());
        };
    }

    private static SmsChannelProperties buildProperties(SmsChannelDO channel) {
        return new SmsChannelProperties()
                .setId(channel.getId())
                .setSignature(channel.getSignature())
                .setCode(channel.getCode())
                .setApiKey(channel.getApiKey())
                .setApiSecret(channel.getApiSecret())
                .setCallbackUrl(channel.getCallbackUrl());
    }

}
