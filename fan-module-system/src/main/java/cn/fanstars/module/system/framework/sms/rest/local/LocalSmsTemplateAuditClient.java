package cn.fanstars.module.system.framework.sms.rest.local;

import cn.hutool.core.util.StrUtil;
import cn.fanstars.module.system.framework.sms.core.client.SmsClient;
import cn.fanstars.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import cn.fanstars.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import cn.fanstars.module.system.framework.sms.rest.AbstractSmsTemplateAuditClient;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateModifyReqDTO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateSubmitReqDTO;

/**
 * 本地占位审核客户端（华为云 / Debug 等不支持云平台模板 CRUD 的渠道）
 */
public class LocalSmsTemplateAuditClient extends AbstractSmsTemplateAuditClient {

    private final String channelCodePrefix;

    public LocalSmsTemplateAuditClient(SmsClient smsClient, String channelCodePrefix) {
        super(smsClient);
        this.channelCodePrefix = channelCodePrefix;
    }

    @Override
    public SmsTemplateRespDTO submitTemplate(SmsTemplateSubmitReqDTO req) {
        return new SmsTemplateRespDTO()
                .setId(channelCodePrefix + "_" + req.getCode())
                .setContent(req.getContent())
                .setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus())
                .setAuditReason(null);
    }

    @Override
    public SmsTemplateRespDTO modifyTemplate(SmsTemplateModifyReqDTO req) {
        String apiTemplateId = req.getApiTemplateId();
        if (StrUtil.isBlank(apiTemplateId)) {
            apiTemplateId = channelCodePrefix + "_MODIFY";
        }
        return new SmsTemplateRespDTO()
                .setId(apiTemplateId)
                .setContent(req.getContent())
                .setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus())
                .setAuditReason(null);
    }

}
