package cn.fanstars.module.mp.service.forward.log;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.mp.controller.admin.forward.log.vo.MessageForwardLogPageReqVO;
import cn.fanstars.module.mp.controller.admin.forward.log.vo.MessageForwardLogSaveReqVO;
import cn.fanstars.module.mp.dal.dataobject.forward.log.MessageForwardLogDO;
import cn.fanstars.module.mp.dal.mysql.forward.log.MessageForwardLogMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.mp.enums.ErrorCodeConstants.MESSAGE_FORWARD_LOG_NOT_EXISTS;

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
    public Long createMessageForwardLog(MessageForwardLogSaveReqVO createReqVO) {
        // 插入
        MessageForwardLogDO messageForwardLog = BeanUtils.toBean(createReqVO, MessageForwardLogDO.class);
        messageForwardLogMapper.insert(messageForwardLog);

        // 返回
        return messageForwardLog.getId();
    }

    @Override
    public void updateMessageForwardLog(MessageForwardLogSaveReqVO updateReqVO) {
        // 校验存在
        validateMessageForwardLogExists(updateReqVO.getId());
        // 更新
        MessageForwardLogDO updateObj = BeanUtils.toBean(updateReqVO, MessageForwardLogDO.class);
        messageForwardLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteMessageForwardLog(Long id) {
        // 校验存在
        validateMessageForwardLogExists(id);
        // 删除
        messageForwardLogMapper.deleteById(id);
    }

    @Override
    public void deleteMessageForwardLogListByIds(List<Long> ids) {
        // 删除
        messageForwardLogMapper.deleteByIds(ids);
    }


    private void validateMessageForwardLogExists(Long id) {
        if (messageForwardLogMapper.selectById(id) == null) {
            throw exception(MESSAGE_FORWARD_LOG_NOT_EXISTS);
        }
    }

    @Override
    public MessageForwardLogDO getMessageForwardLog(Long id) {
        return messageForwardLogMapper.selectById(id);
    }

    @Override
    public PageResult<MessageForwardLogDO> getMessageForwardLogPage(MessageForwardLogPageReqVO pageReqVO) {
        return messageForwardLogMapper.selectPage(pageReqVO);
    }

}