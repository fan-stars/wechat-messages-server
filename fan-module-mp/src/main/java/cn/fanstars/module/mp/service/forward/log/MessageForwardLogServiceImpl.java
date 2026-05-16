package cn.fanstars.module.mp.service.forward.log;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.module.mp.controller.admin.forward.log.vo.MessageForwardLogPageReqVO;
import cn.fanstars.module.mp.dal.dataobject.forward.log.MessageForwardLogDO;
import cn.fanstars.module.mp.dal.mysql.forward.log.MessageForwardLogMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 转发日志 Service 实现类
 *
 * @author 繁星源码
 */
@Service
@Validated
public class MessageForwardLogServiceImpl implements MessageForwardLogService {

    @Resource
    private MessageForwardLogMapper messageForwardLogMapper;

    @Override
    public MessageForwardLogDO getMessageForwardLog(Long id) {
        return messageForwardLogMapper.selectById(id);
    }

    @Override
    public PageResult<MessageForwardLogDO> getMessageForwardLogPage(MessageForwardLogPageReqVO pageReqVO) {
        return messageForwardLogMapper.selectPage(pageReqVO);
    }

    @Override
    public void createMessageForwardLog(MessageForwardLogDO logDO) {
        messageForwardLogMapper.insert(logDO);
    }

}
