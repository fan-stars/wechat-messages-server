package cn.fanstars.module.mp.dal.mysql.forward.log;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.mybatis.core.mapper.BaseMapperX;
import cn.fanstars.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fanstars.module.mp.controller.admin.forward.log.vo.MessageForwardLogPageReqVO;
import cn.fanstars.module.mp.dal.dataobject.forward.log.MessageForwardLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 转发日志 Mapper
 *
 * @author 繁星源码
 */
@Mapper
public interface MessageForwardLogMapper extends BaseMapperX<MessageForwardLogDO> {

    default PageResult<MessageForwardLogDO> selectPage(MessageForwardLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageForwardLogDO>()
                .eqIfPresent(MessageForwardLogDO::getRuleId, reqVO.getRuleId())
                .eqIfPresent(MessageForwardLogDO::getMessageId, reqVO.getMessageId())
                .eqIfPresent(MessageForwardLogDO::getAccountId, reqVO.getAccountId())
                .likeIfPresent(MessageForwardLogDO::getOpenid, reqVO.getOpenid())
                .eqIfPresent(MessageForwardLogDO::getForwardMode, reqVO.getForwardMode())
                .eqIfPresent(MessageForwardLogDO::getStatus, reqVO.getStatus())
                .likeIfPresent(MessageForwardLogDO::getErrorMsg, reqVO.getErrorMsg())
                .betweenIfPresent(MessageForwardLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MessageForwardLogDO::getId));
    }

}