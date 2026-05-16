package cn.fanstars.module.mp.dal.mysql.forward.rule;

import cn.fanstars.framework.common.enums.CommonStatusEnum;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.mybatis.core.mapper.BaseMapperX;
import cn.fanstars.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fanstars.module.mp.controller.admin.forward.rule.vo.MessageForwardRulePageReqVO;
import cn.fanstars.module.mp.dal.dataobject.forward.rule.MessageForwardRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 转发规则 Mapper
 *
 * @author 繁星源码
 */
@Mapper
public interface MessageForwardRuleMapper extends BaseMapperX<MessageForwardRuleDO> {

    default PageResult<MessageForwardRuleDO> selectPage(MessageForwardRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MessageForwardRuleDO>()
                .eqIfPresent(MessageForwardRuleDO::getAccountId, reqVO.getAccountId())
                .likeIfPresent(MessageForwardRuleDO::getName, reqVO.getName())
                .eqIfPresent(MessageForwardRuleDO::getStatus, reqVO.getStatus())
                .eqIfPresent(MessageForwardRuleDO::getForwardMode, reqVO.getForwardMode())
                .betweenIfPresent(MessageForwardRuleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MessageForwardRuleDO::getPriority)
                .orderByAsc(MessageForwardRuleDO::getId));
    }

    default List<MessageForwardRuleDO> selectEnabledListByAccountId(Long accountId) {
        return selectList(new LambdaQueryWrapperX<MessageForwardRuleDO>()
                .eq(MessageForwardRuleDO::getAccountId, accountId)
                .eq(MessageForwardRuleDO::getStatus, CommonStatusEnum.ENABLE.getStatus())
                .orderByDesc(MessageForwardRuleDO::getPriority)
                .orderByAsc(MessageForwardRuleDO::getId));
    }

}