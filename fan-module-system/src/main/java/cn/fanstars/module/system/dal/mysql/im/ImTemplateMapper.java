package cn.fanstars.module.system.dal.mysql.im;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.mybatis.core.mapper.BaseMapperX;
import cn.fanstars.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fanstars.module.system.controller.admin.im.vo.template.ImTemplatePageReqVO;
import cn.fanstars.module.system.dal.dataobject.im.ImTemplateDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImTemplateMapper extends BaseMapperX<ImTemplateDO> {

    default ImTemplateDO selectByCode(String code) {
        return selectOne(ImTemplateDO::getCode, code);
    }

    default PageResult<ImTemplateDO> selectPage(ImTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImTemplateDO>()
                .likeIfPresent(ImTemplateDO::getCode, reqVO.getCode())
                .likeIfPresent(ImTemplateDO::getName, reqVO.getName())
                .eqIfPresent(ImTemplateDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImTemplateDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImTemplateDO::getId));
    }

}
