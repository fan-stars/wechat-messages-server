package cn.fanstars.module.system.dal.mysql.notice;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.mybatis.core.mapper.BaseMapperX;
import cn.fanstars.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fanstars.module.system.controller.admin.notice.vo.NoticePageReqVO;
import cn.fanstars.module.system.dal.dataobject.notice.NoticeDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapperX<NoticeDO> {

    default PageResult<NoticeDO> selectPage(NoticePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<NoticeDO>()
                .likeIfPresent(NoticeDO::getTitle, reqVO.getTitle())
                .eqIfPresent(NoticeDO::getStatus, reqVO.getStatus())
                .orderByDesc(NoticeDO::getId));
    }

}
