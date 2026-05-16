package cn.fanstars.module.mp.dal.mysql.message;

import cn.fanstars.framework.mybatis.core.mapper.BaseMapperX;
import cn.fanstars.module.mp.controller.admin.message.vo.template.MpMessageTemplateListReqVO;
import cn.fanstars.module.mp.dal.dataobject.message.MpMessageTemplateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MpMessageTemplateMapper extends BaseMapperX<MpMessageTemplateDO> {

    default List<MpMessageTemplateDO> selectList(MpMessageTemplateListReqVO listReqVO) {
        return selectList(MpMessageTemplateDO::getAccountId, listReqVO.getAccountId());
    }

}