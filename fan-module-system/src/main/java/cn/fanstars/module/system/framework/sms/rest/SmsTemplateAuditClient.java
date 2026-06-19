package cn.fanstars.module.system.framework.sms.rest;

import cn.fanstars.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateModifyReqDTO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateSubmitReqDTO;

/**
 * 短信模板云平台审核客户端
 */
public interface SmsTemplateAuditClient {

    /**
     * 提交模板到云平台审核
     *
     * @param req 提交参数
     * @return 云平台模板信息
     */
    SmsTemplateRespDTO submitTemplate(SmsTemplateSubmitReqDTO req) throws Throwable;

    /**
     * 修改模板并重新进入审核
     *
     * @param req 修改参数
     * @return 云平台模板信息
     */
    SmsTemplateRespDTO modifyTemplate(SmsTemplateModifyReqDTO req) throws Throwable;

    /**
     * 同步云平台审核状态
     *
     * @param apiTemplateId API 模板编号
     * @return 云平台模板信息
     */
    SmsTemplateRespDTO syncTemplate(String apiTemplateId) throws Throwable;

}
