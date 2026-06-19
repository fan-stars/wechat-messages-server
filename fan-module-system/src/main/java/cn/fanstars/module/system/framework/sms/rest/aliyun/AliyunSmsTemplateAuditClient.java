package cn.fanstars.module.system.framework.sms.rest.aliyun;

import cn.hutool.core.util.StrUtil;
import cn.fanstars.module.system.framework.sms.core.client.SmsClient;
import cn.fanstars.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import cn.fanstars.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import cn.fanstars.module.system.framework.sms.rest.AbstractSmsTemplateAuditClient;
import cn.fanstars.module.system.framework.sms.rest.api.request.AliyunSmsTemplateCreateReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.request.AliyunSmsTemplateModifyReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.AliyunSmsTemplateRespVO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateModifyReqDTO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateSubmitReqDTO;
import cn.fanstars.module.system.framework.sms.rest.service.AliyunSmsRestService;
import cn.fanstars.module.system.enums.sms.SmsTemplateTypeEnum;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.SMS_TEMPLATE_SUBMIT_AUDIT_FAIL;

/**
 * 阿里云短信模板审核客户端
 */
public class AliyunSmsTemplateAuditClient extends AbstractSmsTemplateAuditClient {

    private final AliyunSmsRestService restService;

    public AliyunSmsTemplateAuditClient(SmsClient smsClient, AliyunSmsRestService restService) {
        super(smsClient);
        this.restService = restService;
    }

    @Override
    public SmsTemplateRespDTO submitTemplate(SmsTemplateSubmitReqDTO req) throws Throwable {
        AliyunSmsTemplateCreateReqVO reqVO = new AliyunSmsTemplateCreateReqVO();
        reqVO.setTemplateType(convertTemplateType(req.getType()));
        reqVO.setTemplateName(req.getName());
        reqVO.setTemplateContent(req.getContent());
        if (StrUtil.isNotBlank(req.getRemark())) {
            reqVO.setRemark(req.getRemark());
        }
        AliyunSmsTemplateRespVO response = restService.addSmsTemplate(reqVO);
        return parseTemplateResponse(response);
    }

    @Override
    public SmsTemplateRespDTO modifyTemplate(SmsTemplateModifyReqDTO req) throws Throwable {
        AliyunSmsTemplateModifyReqVO reqVO = new AliyunSmsTemplateModifyReqVO();
        reqVO.setTemplateCode(req.getApiTemplateId());
        reqVO.setTemplateName(req.getName());
        reqVO.setTemplateContent(req.getContent());
        if (StrUtil.isNotBlank(req.getRemark())) {
            reqVO.setRemark(req.getRemark());
        }
        AliyunSmsTemplateRespVO response = restService.modifySmsTemplate(reqVO);
        return parseTemplateResponse(response);
    }

    private SmsTemplateRespDTO parseTemplateResponse(AliyunSmsTemplateRespVO response) {
        if (!response.isSuccess()) {
            throw exception(SMS_TEMPLATE_SUBMIT_AUDIT_FAIL, response.getMessage());
        }
        return new SmsTemplateRespDTO()
                .setId(response.getTemplateCode())
                .setContent(response.getTemplateContent())
                .setAuditStatus(convertAuditStatus(response.getTemplateStatus()))
                .setAuditReason(response.getReason());
    }

    private static Integer convertTemplateType(Integer type) {
        if (type == null) {
            return 1;
        }
        if (type == SmsTemplateTypeEnum.VERIFICATION_CODE.getType()) {
            return 0;
        }
        if (type == SmsTemplateTypeEnum.NOTICE.getType()) {
            return 1;
        }
        if (type == SmsTemplateTypeEnum.PROMOTION.getType()) {
            return 2;
        }
        return 1;
    }

    private static Integer convertAuditStatus(Integer templateStatus) {
        if (templateStatus == null) {
            return SmsTemplateAuditStatusEnum.CHECKING.getStatus();
        }
        return switch (templateStatus) {
            case 0 -> SmsTemplateAuditStatusEnum.CHECKING.getStatus();
            case 1 -> SmsTemplateAuditStatusEnum.SUCCESS.getStatus();
            case 2 -> SmsTemplateAuditStatusEnum.FAIL.getStatus();
            default -> SmsTemplateAuditStatusEnum.CHECKING.getStatus();
        };
    }

}
