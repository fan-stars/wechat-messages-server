package cn.fanstars.module.mp.service.forward;

import cn.fanstars.framework.tenant.core.util.TenantUtils;
import cn.fanstars.module.mp.controller.admin.open.vo.MpOpenHandleMessageReqVO;
import cn.fanstars.module.mp.dal.dataobject.account.MpAccountDO;
import cn.fanstars.module.mp.dal.dataobject.forward.rule.MessageForwardRuleDO;
import cn.fanstars.module.mp.framework.mp.core.context.MpContextHolder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步消息转发执行器（独立 Bean 以保证 {@link Async} 代理生效）
 * <p>
 * 并行编排由 {@link cn.fanstars.module.mp.service.message.MpMessageReplyOrchestrator} 在
 * {@code mpMessageHandleExecutor} 中直接调用 {@link MessageForwardExecuteServiceImpl#executeForwardRule}，
 * 避免二次调度导致 messageId 丢失。本类供 {@link MessageForwardExecuteServiceImpl#execute} 串行降级路径使用。
 */
@Component
@Slf4j
public class MessageForwardAsyncExecutor {

    @Resource
    @Lazy
    private MessageForwardExecuteServiceImpl messageForwardExecuteService;

    /**
     * 在 Spring 异步线程池中执行单条异步转发规则
     *
     * @param messageId 已入库消息编号（须由调用方在入库后传入）
     */
    @Async
    public void executeAsync(MpAccountDO account, WxMpXmlMessage inMessage, String rawContent,
                             MpOpenHandleMessageReqVO reqVO, Long messageId, MessageForwardRuleDO rule) {
        TenantUtils.execute(account.getTenantId(), () -> {
            MpContextHolder.setAppId(account.getAppId());
            try {
                messageForwardExecuteService.executeForwardRule(account, inMessage, rawContent, reqVO, messageId, rule);
            } finally {
                MpContextHolder.clear();
            }
        });
    }

}
