package cn.fanstars.module.system.framework.sms.rest.qiniu;

import cn.hutool.core.util.StrUtil;
import cn.fanstars.module.system.framework.sms.core.client.SmsClient;
import cn.fanstars.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import cn.fanstars.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import cn.fanstars.module.system.framework.sms.rest.AbstractSmsTemplateAuditClient;
import cn.fanstars.module.system.framework.sms.rest.api.request.QiniuSmsTemplateSaveReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.QiniuSmsTemplateRespVO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateModifyReqDTO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateSubmitReqDTO;
import cn.fanstars.module.system.framework.sms.rest.service.QiniuSmsRestService;
import cn.fanstars.module.system.enums.sms.SmsTemplateTypeEnum;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.SMS_TEMPLATE_SUBMIT_AUDIT_FAIL;

/**
 * 七牛云短信模板审核客户端
 */
public class QiniuSmsTemplateAuditClient extends AbstractSmsTemplateAuditClient {

    private final QiniuSmsRestService restService;

    public QiniuSmsTemplateAuditClient(SmsClient smsClient, QiniuSmsRestService restService) {
        super(smsClient);
        this.restService = restService;
    }

    @Override
    public SmsTemplateRespDTO submitTemplate(SmsTemplateSubmitReqDTO req) throws Throwable {
        QiniuSmsTemplateSaveReqVO reqVO = buildSaveReqVO(req.getName(), req.getContent(), req.getType());
        QiniuSmsTemplateRespVO response = restService.createTemplate(reqVO);
        return parseResponse(response, req.getContent());
    }

    @Override
    public SmsTemplateRespDTO modifyTemplate(SmsTemplateModifyReqDTO req) throws Throwable {
        QiniuSmsTemplateSaveReqVO reqVO = buildSaveReqVO(req.getName(), req.getContent(), req.getType());
        QiniuSmsTemplateRespVO response = restService.updateTemplate(req.getApiTemplateId(), reqVO);
        return parseResponse(response, req.getContent());
    }

    private QiniuSmsTemplateSaveReqVO buildSaveReqVO(String name, String content, Integer type) {
        QiniuSmsTemplateSaveReqVO reqVO = new QiniuSmsTemplateSaveReqVO();
        reqVO.setName(name);
        reqVO.setTemplate(content);
        reqVO.setType(convertType(type));
        String signature = restService.getChannelProperties().getSignature();
        if (StrUtil.isNotBlank(signature)) {
            reqVO.setSignature(signature);
        }
        return reqVO;
    }

    private SmsTemplateRespDTO parseResponse(QiniuSmsTemplateRespVO response, String content) {
        if (response.hasError()) {
            throw exception(SMS_TEMPLATE_SUBMIT_AUDIT_FAIL, response.getMessage());
        }
        return new SmsTemplateRespDTO()
                .setId(response.getId())
                .setContent(content)
                .setAuditStatus(SmsTemplateAuditStatusEnum.CHECKING.getStatus());
    }

    private static String convertType(Integer type) {
        if (type != null && type == SmsTemplateTypeEnum.PROMOTION.getType()) {
            return "marketing";
        }
        if (type != null && type == SmsTemplateTypeEnum.VERIFICATION_CODE.getType()) {
            return "verification";
        }
        return "notification";
    }

}
