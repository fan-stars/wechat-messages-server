package cn.fanstars.module.system.framework.sms.core.client.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.fanstars.framework.common.core.KeyValue;
import cn.fanstars.module.system.framework.sms.core.client.dto.SmsReceiveRespDTO;
import cn.fanstars.module.system.framework.sms.core.client.dto.SmsSendRespDTO;
import cn.fanstars.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.api.request.SubmailSmsSendReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.SubmailSmsRespVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.SubmailSmsTemplateQueryRespVO;
import cn.fanstars.module.system.framework.sms.rest.factory.SmsRestServiceFactory;
import cn.fanstars.module.system.framework.sms.rest.service.SubmailSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.support.SubmailContentConverter;
import cn.fanstars.module.system.framework.sms.rest.support.SubmailSmsPropertiesSupport;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 赛邮短信客户端
 */
@Slf4j
public class SubmailSmsClient extends AbstractSmsClient {

    public SubmailSmsClient(SmsChannelProperties properties) {
        super(properties);
        Assert.notEmpty(properties.getApiKey(), "apiKey 不能为空");
        Assert.notEmpty(properties.getApiSecret(), "apiSecret 不能为空");
    }

    @Override
    public SmsSendRespDTO sendSms(Long sendLogId, String mobile, String apiTemplateId,
                                  List<KeyValue<String, Object>> templateParams) throws Throwable {
        SubmailSmsSendReqVO reqVO = new SubmailSmsSendReqVO();
        reqVO.setTo(mobile);
        reqVO.setTemplateId(apiTemplateId);
        reqVO.setSign(properties.getSignature());
        Map<String, String> vars = CollStreamUtil.toMap(templateParams, KeyValue::getKey,
                item -> String.valueOf(item.getValue()));
        reqVO.setVars(vars);
        SubmailSmsRespVO response = getRestService().sendSms(reqVO);
        if (!response.isSuccess()) {
            return new SmsSendRespDTO().setSuccess(false)
                    .setApiCode(String.valueOf(response.getCode()))
                    .setApiMsg(response.getMsg());
        }
        return new SmsSendRespDTO().setSuccess(true)
                .setSerialNo(StrUtil.blankToDefault(response.getSendId(), String.valueOf(sendLogId)));
    }

    @Override
    public List<SmsReceiveRespDTO> parseSmsReceiveStatus(String text) {
        return Collections.emptyList();
    }

    @Override
    public SmsTemplateRespDTO getSmsTemplate(String apiTemplateId) throws Throwable {
        SubmailSmsTemplateQueryRespVO response = getRestService().getTemplate(apiTemplateId);
        if (!response.isSuccess()) {
            throw new IllegalStateException(StrUtil.blankToDefault(response.getMsg(), "查询赛邮短信模板失败"));
        }
        SubmailSmsTemplateQueryRespVO.TemplateDTO template = response.getTemplate();
        if (template == null) {
            throw new IllegalStateException("赛邮短信模板不存在");
        }
        return new SmsTemplateRespDTO()
                .setId(template.getTemplateId())
                .setContent(SubmailContentConverter.fromSubmailPlaceholders(template.getContent()))
                .setAuditStatus(SubmailSmsPropertiesSupport.convertAuditStatus(template.getTemplateStatus()))
                .setAuditReason(template.getTemplateRejectReason());
    }

    private SubmailSmsRestService getRestService() {
        return SpringUtil.getBean(SmsRestServiceFactory.class).getSubmailRestService(properties);
    }

}
