package cn.fanstars.module.system.framework.sms.rest;

import cn.fanstars.module.system.framework.sms.core.client.SmsClient;
import cn.fanstars.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import lombok.RequiredArgsConstructor;

/**
 * 短信模板审核客户端抽象类：同步状态委托既有 {@link SmsClient}
 */
@RequiredArgsConstructor
public abstract class AbstractSmsTemplateAuditClient implements SmsTemplateAuditClient {

    protected final SmsClient smsClient;

    @Override
    public SmsTemplateRespDTO syncTemplate(String apiTemplateId) throws Throwable {
        return smsClient.getSmsTemplate(apiTemplateId);
    }

}
