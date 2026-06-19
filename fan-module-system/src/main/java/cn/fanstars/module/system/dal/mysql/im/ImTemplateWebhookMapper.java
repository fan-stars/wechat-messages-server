package cn.fanstars.module.system.dal.mysql.im;

import cn.fanstars.framework.mybatis.core.mapper.BaseMapperX;
import cn.fanstars.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fanstars.module.system.dal.dataobject.im.ImTemplateWebhookDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ImTemplateWebhookMapper extends BaseMapperX<ImTemplateWebhookDO> {

    default List<Long> selectWebhookIdsByTemplateId(Long templateId) {
        return selectList(ImTemplateWebhookDO::getTemplateId, templateId).stream()
                .map(ImTemplateWebhookDO::getWebhookId)
                .collect(Collectors.toList());
    }

    default void deleteByTemplateId(Long templateId) {
        delete(new LambdaQueryWrapperX<ImTemplateWebhookDO>()
                .eq(ImTemplateWebhookDO::getTemplateId, templateId));
    }

    default List<ImTemplateWebhookDO> selectListByTemplateIds(Collection<Long> templateIds) {
        return selectList(ImTemplateWebhookDO::getTemplateId, templateIds);
    }

}
