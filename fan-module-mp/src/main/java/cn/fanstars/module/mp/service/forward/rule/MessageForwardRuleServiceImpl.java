package cn.fanstars.module.mp.service.forward.rule;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.mp.controller.admin.forward.rule.vo.MessageForwardRulePageReqVO;
import cn.fanstars.module.mp.controller.admin.forward.rule.vo.MessageForwardRuleSaveReqVO;
import cn.fanstars.module.mp.dal.dataobject.forward.rule.MessageForwardRuleDO;
import cn.fanstars.module.mp.dal.mysql.forward.rule.MessageForwardRuleMapper;
import cn.fanstars.module.mp.enums.forward.MessageForwardModeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.mp.enums.ErrorCodeConstants.MESSAGE_FORWARD_RULE_INVALID;
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
        validateMessageForwardRule(createReqVO);
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
        validateMessageForwardRule(updateReqVO);
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

    private void validateMessageForwardRule(MessageForwardRuleSaveReqVO reqVO) {
        if (Objects.equals(reqVO.getForwardMode(), MessageForwardModeEnum.ASYNC.getMode())) {
            if (Boolean.TRUE.equals(reqVO.getReceiveResponse()) || Boolean.TRUE.equals(reqVO.getUseResponseAsReply())) {
                throw exception(MESSAGE_FORWARD_RULE_INVALID, "异步模式不能接收响应用于回复微信");
            }
        }
        if (Boolean.TRUE.equals(reqVO.getUseResponseAsReply())) {
            if (!Boolean.TRUE.equals(reqVO.getReceiveResponse())
                    || !Objects.equals(reqVO.getForwardMode(), MessageForwardModeEnum.SYNC.getMode())) {
                throw exception(MESSAGE_FORWARD_RULE_INVALID, "作为微信回复须为同步模式且接收响应");
            }
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