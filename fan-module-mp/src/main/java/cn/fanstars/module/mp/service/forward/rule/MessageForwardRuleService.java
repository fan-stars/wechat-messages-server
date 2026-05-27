package cn.fanstars.module.mp.service.forward.rule;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.module.mp.controller.admin.forward.rule.vo.MessageForwardRulePageReqVO;
import cn.fanstars.module.mp.controller.admin.forward.rule.vo.MessageForwardRuleSaveReqVO;
import cn.fanstars.module.mp.dal.dataobject.forward.rule.MessageForwardRuleDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * 转发规则 Service 接口
 *
 * @author 繁星源码
 */
public interface MessageForwardRuleService {

    /**
     * 创建转发规则
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMessageForwardRule(@Valid MessageForwardRuleSaveReqVO createReqVO);

    /**
     * 更新转发规则
     *
     * @param updateReqVO 更新信息
     */
    void updateMessageForwardRule(@Valid MessageForwardRuleSaveReqVO updateReqVO);

    /**
     * 删除转发规则
     *
     * @param id 编号
     */
    void deleteMessageForwardRule(Long id);

    /**
     * 批量删除转发规则
     *
     * @param ids 编号
     */
    void deleteMessageForwardRuleListByIds(List<Long> ids);

    /**
     * 获得转发规则
     *
     * @param id 编号
     * @return 转发规则
     */
    MessageForwardRuleDO getMessageForwardRule(Long id);

    /**
     * 获得转发规则分页
     *
     * @param pageReqVO 分页查询
     * @return 转发规则分页
     */
    PageResult<MessageForwardRuleDO> getMessageForwardRulePage(MessageForwardRulePageReqVO pageReqVO);

    /**
     * 获得转发规则精简列表（用于下拉、日志展示等）
     *
     * @return 转发规则列表
     */
    List<MessageForwardRuleDO> getMessageForwardRuleList();

}