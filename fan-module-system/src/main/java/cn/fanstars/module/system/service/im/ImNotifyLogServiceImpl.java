package cn.fanstars.module.system.service.im;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.module.system.controller.admin.im.vo.log.ImNotifyLogPageReqVO;
import cn.fanstars.module.system.dal.dataobject.im.ImNotifyLogDO;
import cn.fanstars.module.system.dal.dataobject.im.ImTemplateDO;
import cn.fanstars.module.system.dal.dataobject.im.ImWebhookDO;
import cn.fanstars.module.system.dal.mysql.im.ImNotifyLogMapper;
import cn.fanstars.module.system.enums.im.ImSendStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * IM 发送日志 Service 实现类
 *
 * @author 繁星源码
 */
@Service
public class ImNotifyLogServiceImpl implements ImNotifyLogService {

    @Resource
    private ImNotifyLogMapper imNotifyLogMapper;

    @Override
    public Long createImNotifyLog(ImTemplateDO template, ImWebhookDO webhook, String templateContent,
                                  Map<String, Object> templateParams, boolean isSend) {
        ImNotifyLogDO log = ImNotifyLogDO.builder()
                .templateId(template.getId())
                .templateCode(template.getCode())
                .webhookId(webhook.getId())
                .platform(webhook.getPlatform())
                .templateContent(templateContent)
                .templateParams(templateParams)
                .sendStatus(Objects.equals(isSend, true) ? ImSendStatusEnum.INIT.getStatus()
                        : ImSendStatusEnum.IGNORE.getStatus())
                .build();
        imNotifyLogMapper.insert(log);
        return log.getId();
    }

    @Override
    public void updateImNotifySendResult(Long id, boolean success, String apiSendCode, String apiSendMsg) {
        ImSendStatusEnum sendStatus = success ? ImSendStatusEnum.SUCCESS : ImSendStatusEnum.FAILURE;
        imNotifyLogMapper.updateById(ImNotifyLogDO.builder()
                .id(id)
                .sendStatus(sendStatus.getStatus())
                .sendTime(LocalDateTime.now())
                .apiSendCode(apiSendCode)
                .apiSendMsg(apiSendMsg)
                .build());
    }

    @Override
    public PageResult<ImNotifyLogDO> getImNotifyLogPage(ImNotifyLogPageReqVO pageReqVO) {
        return imNotifyLogMapper.selectPage(pageReqVO);
    }

    @Override
    public ImNotifyLogDO getImNotifyLog(Long id) {
        return imNotifyLogMapper.selectById(id);
    }

}
