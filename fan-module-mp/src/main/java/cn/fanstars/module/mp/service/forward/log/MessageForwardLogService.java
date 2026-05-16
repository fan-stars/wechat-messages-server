package cn.fanstars.module.mp.service.forward.log;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.module.mp.controller.admin.forward.log.vo.MessageForwardLogPageReqVO;
import cn.fanstars.module.mp.dal.dataobject.forward.log.MessageForwardLogDO;

/**
 * 转发日志 Service 接口
 *
 * @author 繁星源码
 */
public interface MessageForwardLogService {

    /**
     * 获得转发日志
     *
     * @param id 编号
     * @return 转发日志
     */
    MessageForwardLogDO getMessageForwardLog(Long id);

    /**
     * 获得转发日志分页
     *
     * @param pageReqVO 分页查询
     * @return 转发日志分页
     */
    PageResult<MessageForwardLogDO> getMessageForwardLogPage(MessageForwardLogPageReqVO pageReqVO);

}
