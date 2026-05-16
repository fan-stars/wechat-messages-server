package cn.fanstars.module.mp.service.forward.rule;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.mp.controller.admin.forward.rule.vo.MessageForwardRulePageReqVO;
import cn.fanstars.module.mp.controller.admin.forward.rule.vo.MessageForwardRuleSaveReqVO;
import cn.fanstars.module.mp.dal.dataobject.forward.rule.MessageForwardRuleDO;
import cn.fanstars.module.mp.dal.mysql.forward.rule.MessageForwardRuleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.mp.enums.ErrorCodeConstants.MESSAGE_FORWARD_RULE_NOT_EXISTS;

/**
 * 转发规则 Service 实现类
 *
 * @author 繁星源码
 */
@Service
@Validated
public class MessageForwardRuleServiceImpl implements MessageForwardRuleService {

    @Resource
    private MessageForwardRuleMapper messageForwardRuleMapper;

    @Override
    public Long createMessageForwardRule(MessageForwardRuleSaveReqVO createReqVO) {
        // 插入
        MessageForwardRuleDO messageForwardRule = BeanUtils.toBean(createReqVO, MessageForwardRuleDO.class);
        messageForwardRuleMapper.insert(messageForwardRule);

        // 返回
        return messageForwardRule.getId();
    }

    @Override
    public void updateMessageForwardRule(MessageForwardRuleSaveReqVO updateReqVO) {
        // 校验存在
        validateMessageForwardRuleExists(updateReqVO.getId());
        // 更新
        MessageForwardRuleDO updateObj = BeanUtils.toBean(updateReqVO, MessageForwardRuleDO.class);
        messageForwardRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteMessageForwardRule(Long id) {
        // 校验存在
        validateMessageForwardRuleExists(id);
        // 删除
        messageForwardRuleMapper.deleteById(id);
    }

    @Override
    public void deleteMessageForwardRuleListByIds(List<Long> ids) {
        // 删除
        messageForwardRuleMapper.deleteByIds(ids);
    }


    private void validateMessageForwardRuleExists(Long id) {
        if (messageForwardRuleMapper.selectById(id) == null) {
            throw exception(MESSAGE_FORWARD_RULE_NOT_EXISTS);
        }
    }

    @Override
    public MessageForwardRuleDO getMessageForwardRule(Long id) {
        return messageForwardRuleMapper.selectById(id);
    }

    @Override
    public PageResult<MessageForwardRuleDO> getMessageForwardRulePage(MessageForwardRulePageReqVO pageReqVO) {
        return messageForwardRuleMapper.selectPage(pageReqVO);
    }

}