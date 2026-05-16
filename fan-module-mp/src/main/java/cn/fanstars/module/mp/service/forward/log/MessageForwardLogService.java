package cn.fanstars.module.mp.service.forward.log;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.module.mp.controller.admin.forward.log.vo.MessageForwardLogPageReqVO;
import cn.fanstars.module.mp.controller.admin.forward.log.vo.MessageForwardLogSaveReqVO;
import cn.fanstars.module.mp.dal.dataobject.forward.log.MessageForwardLogDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 转发日志 Service 接口
 *
 * @author 繁星源码
 */
public interface MessageForwardLogService {

    /**
     * 创建转发日志
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMessageForwardLog(@Valid MessageForwardLogSaveReqVO createReqVO);

    /**
     * 更新转发日志
     *
     * @param updateReqVO 更新信息
     */
    void updateMessageForwardLog(@Valid MessageForwardLogSaveReqVO updateReqVO);

    /**
     * 删除转发日志
     *
     * @param id 编号
     */
    void deleteMessageForwardLog(Long id);

    /**
     * 批量删除转发日志
     *
     * @param ids 编号
     */
    void deleteMessageForwardLogListByIds(List<Long> ids);

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