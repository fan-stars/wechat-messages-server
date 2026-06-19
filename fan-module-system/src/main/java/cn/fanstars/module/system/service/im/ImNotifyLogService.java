package cn.fanstars.module.system.service.im;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.module.system.controller.admin.im.vo.log.ImNotifyLogPageReqVO;
import cn.fanstars.module.system.dal.dataobject.im.ImNotifyLogDO;
import cn.fanstars.module.system.dal.dataobject.im.ImTemplateDO;
import cn.fanstars.module.system.dal.dataobject.im.ImWebhookDO;

import java.util.Map;

/**
 * IM 发送日志 Service
 *
 * @author 繁星源码
 */
public interface ImNotifyLogService {

    Long createImNotifyLog(ImTemplateDO template, ImWebhookDO webhook, String templateContent,
                           Map<String, Object> templateParams, boolean isSend);

    void updateImNotifySendResult(Long id, boolean success, String apiSendCode, String apiSendMsg);

    PageResult<ImNotifyLogDO> getImNotifyLogPage(ImNotifyLogPageReqVO pageReqVO);

    ImNotifyLogDO getImNotifyLog(Long id);

}
