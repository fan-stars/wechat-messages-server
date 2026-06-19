package cn.fanstars.module.system.service.sms;

import cn.fanstars.module.system.dal.dataobject.sms.SmsTemplateDO;

import java.util.List;

/**
 * 短信模板 Service 扩展接口（Fan）
 */
public interface FanSmsTemplateService extends SmsTemplateService {

    /**
     * 对已有模板补提交云平台审核
     *
     * @param id 模板编号
     */
    void submitAudit(Long id);

    /**
     * 同步单条模板审核状态
     *
     * @param id 模板编号
     * @return 最新模板
     */
    SmsTemplateDO syncAuditStatus(Long id);

    /**
     * 批量同步模板审核状态
     *
     * @param ids 模板编号列表
     */
    void syncAuditStatusList(List<Long> ids);

}
