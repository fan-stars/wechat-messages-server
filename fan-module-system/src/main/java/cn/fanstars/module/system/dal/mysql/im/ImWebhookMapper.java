package cn.fanstars.module.system.dal.mysql.im;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.mybatis.core.mapper.BaseMapperX;
import cn.fanstars.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fanstars.module.system.controller.admin.im.vo.webhook.ImWebhookPageReqVO;
import cn.fanstars.module.system.dal.dataobject.im.ImWebhookDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImWebhookMapper extends BaseMapperX<ImWebhookDO> {

    default PageResult<ImWebhookDO> selectPage(ImWebhookPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImWebhookDO>()
                .likeIfPresent(ImWebhookDO::getName, reqVO.getName())
                .eqIfPresent(ImWebhookDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(ImWebhookDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImWebhookDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImWebhookDO::getId));
    }

    default List<ImWebhookDO> selectListByStatus(Integer status) {
        return selectList(ImWebhookDO::getStatus, status);
    }

}
