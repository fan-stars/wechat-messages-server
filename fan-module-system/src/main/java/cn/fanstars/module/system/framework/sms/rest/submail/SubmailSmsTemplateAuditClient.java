package cn.fanstars.module.system.framework.sms.rest.submail;

import cn.hutool.core.util.StrUtil;
import cn.fanstars.module.system.framework.sms.core.client.SmsClient;
import cn.fanstars.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import cn.fanstars.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import cn.fanstars.module.system.framework.sms.rest.AbstractSmsTemplateAuditClient;
import cn.fanstars.module.system.framework.sms.rest.api.request.SubmailSmsTemplateSaveReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.SubmailSmsRespVO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateModifyReqDTO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateSubmitReqDTO;
import cn.fanstars.module.system.framework.sms.rest.service.SubmailSmsRestService;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.SMS_TEMPLATE_SUBMIT_AUDIT_FAIL;

/**
 * 赛邮短信模板审核客户端
 */
public class SubmailSmsTemplateAuditClient extends AbstractSmsTemplateAuditClient {

    private final SubmailSmsRestService restService;

    public SubmailSmsTemplateAuditClient(SmsClient smsClient, SubmailSmsRestService restService) {
        super(smsClient);
        this.restService = restService;
    }

    @Override
    public SmsTemplateRespDTO submitTemplate(SmsTemplateSubmitReqDTO req) throws Throwable {
        SubmailSmsTemplateSaveReqVO reqVO = buildSaveReqVO(req.getName(), req.getContent(), null);
        SubmailSmsRespVO response = restService.createTemplate(reqVO);
        return parseMutationResponse(response, req.getContent());
    }

    @Override
    public SmsTemplateRespDTO modifyTemplate(SmsTemplateModifyReqDTO req) throws Throwable {
        SubmailSmsTemplateSaveReqVO reqVO = buildSaveReqVO(req.getName(), req.getContent(), req.getApiTemplateId());
        SubmailSmsRespVO response = restService.updateTemplate(reqVO);
        return parseMutationResponse(response, req.getContent());
    }

    private SubmailSmsTemplateSaveReqVO buildSaveReqVO(String title, String content, String templateId) {
        SubmailSmsTemplateSaveReqVO reqVO = new SubmailSmsTemplateSaveReqVO();
        reqVO.setTitle(title);
        reqVO.setContent(content);
        reqVO.setTemplateId(templateId);
        String signature = restService.getChannelProperties().getSignature();
        if (StrUtil.isNotBlank(signature)) {
            reqVO.setSignature(signature);
        }
        return reqVO;
    }

    private SmsTemplateRespDTO parseMutationResponse(SubmailSmsRespVO response, String content) {
        if (!response.isSuccess()) {
            throw exception(SMS_TEMPLATE_SUBMIT_AUDIT_FAIL, response.getMsg());
        }
        return new SmsTemplateRespDTO()
                .setId(response.getTemplateId())
                .setContent(content)
                .setAuditStatus(SmsTemplateAuditStatusEnum.CHECKING.getStatus());
    }

}
