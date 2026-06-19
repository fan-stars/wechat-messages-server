package cn.fanstars.module.system.framework.sms.rest.tencent;

import cn.fanstars.module.system.framework.sms.core.client.SmsClient;
import cn.fanstars.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import cn.fanstars.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import cn.fanstars.module.system.framework.sms.rest.AbstractSmsTemplateAuditClient;
import cn.fanstars.module.system.framework.sms.rest.api.request.TencentSmsTemplateCreateReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.request.TencentSmsTemplateModifyReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.TencentSmsTemplateRespVO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateModifyReqDTO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateSubmitReqDTO;
import cn.fanstars.module.system.framework.sms.rest.service.TencentSmsRestService;
import cn.fanstars.module.system.enums.sms.SmsTemplateTypeEnum;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.SMS_TEMPLATE_SUBMIT_AUDIT_FAIL;

/**
 * 腾讯云短信模板审核客户端
 */
public class TencentSmsTemplateAuditClient extends AbstractSmsTemplateAuditClient {

    private final TencentSmsRestService restService;

    public TencentSmsTemplateAuditClient(SmsClient smsClient, TencentSmsRestService restService) {
        super(smsClient);
        this.restService = restService;
    }

    @Override
    public SmsTemplateRespDTO submitTemplate(SmsTemplateSubmitReqDTO req) throws Throwable {
        TencentSmsTemplateCreateReqVO reqVO = new TencentSmsTemplateCreateReqVO();
        reqVO.setTemplateName(req.getName());
        reqVO.setTemplateContent(req.getContent());
        reqVO.setSmsType(convertSmsType(req.getType()));
        reqVO.setRemark(req.getRemark());
        TencentSmsTemplateRespVO response = restService.addSmsTemplate(reqVO);
        return parseAddOrModifyResponse(response);
    }

    @Override
    public SmsTemplateRespDTO modifyTemplate(SmsTemplateModifyReqDTO req) throws Throwable {
        TencentSmsTemplateModifyReqVO reqVO = new TencentSmsTemplateModifyReqVO();
        reqVO.setTemplateId(Long.valueOf(req.getApiTemplateId()));
        reqVO.setTemplateName(req.getName());
        reqVO.setTemplateContent(req.getContent());
        reqVO.setSmsType(convertSmsType(req.getType()));
        reqVO.setRemark(req.getRemark());
        TencentSmsTemplateRespVO response = restService.modifySmsTemplate(reqVO);
        return parseAddOrModifyResponse(response);
    }

    private SmsTemplateRespDTO parseAddOrModifyResponse(TencentSmsTemplateRespVO response) {
        if (response.hasError()) {
            throw exception(SMS_TEMPLATE_SUBMIT_AUDIT_FAIL, response.getError().getMessage());
        }
        return new SmsTemplateRespDTO()
                .setId(response.getTemplateId())
                .setAuditStatus(SmsTemplateAuditStatusEnum.CHECKING.getStatus());
    }

    private static long convertSmsType(Integer type) {
        if (type != null && type == SmsTemplateTypeEnum.PROMOTION.getType()) {
            return 1L;
        }
        return 0L;
    }

}
