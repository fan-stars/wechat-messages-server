package cn.fanstars.module.system.service.sms;

import cn.fanstars.module.system.dal.dataobject.sms.SmsTemplateDO;
import cn.fanstars.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.SMS_TEMPLATE_AUDIT_NOT_PASSED;

/**
 * 短信发送 Service Fan 实现：发送前校验模板审核状态
 */
@Service
@Primary
@Slf4j
public class FanSmsSendServiceImpl extends SmsSendServiceImpl {

    @Override
    public Long sendSingleSms(String mobile, Long userId, Integer userType,
                              String templateCode, Map<String, Object> templateParams) {
        SmsTemplateDO template = validateSmsTemplate(templateCode);
        if (!Objects.equals(template.getAuditStatus(), SmsTemplateAuditStatusEnum.SUCCESS.getStatus())) {
            throw exception(SMS_TEMPLATE_AUDIT_NOT_PASSED);
        }
        return super.sendSingleSms(mobile, userId, userType, templateCode, templateParams);
    }

}
