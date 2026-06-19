package cn.fanstars.module.system.dal.mysql.im;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.mybatis.core.mapper.BaseMapperX;
import cn.fanstars.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fanstars.module.system.controller.admin.im.vo.log.ImNotifyLogPageReqVO;
import cn.fanstars.module.system.dal.dataobject.im.ImNotifyLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImNotifyLogMapper extends BaseMapperX<ImNotifyLogDO> {

    default PageResult<ImNotifyLogDO> selectPage(ImNotifyLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImNotifyLogDO>()
                .likeIfPresent(ImNotifyLogDO::getTemplateCode, reqVO.getTemplateCode())
                .eqIfPresent(ImNotifyLogDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(ImNotifyLogDO::getSendStatus, reqVO.getSendStatus())
                .eqIfPresent(ImNotifyLogDO::getWebhookId, reqVO.getWebhookId())
                .betweenIfPresent(ImNotifyLogDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImNotifyLogDO::getId));
    }

}
