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
 * 异步消息转发执行器（独立 Bean 以保证 @Async 生效）
 */
@Component
@Slf4j
public class MessageForwardAsyncExecutor {

    @Resource
    @Lazy
    private MessageForwardExecuteServiceImpl messageForwardExecuteService;

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
