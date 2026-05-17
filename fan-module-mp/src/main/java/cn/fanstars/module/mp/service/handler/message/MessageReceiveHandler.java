package cn.fanstars.module.mp.service.handler.message;

import cn.fanstars.module.mp.framework.mp.core.context.MpContextHolder;
import cn.fanstars.module.mp.service.message.MpMessageService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Map;

/**
 * 微信消息入库 Handler（WxMpMessageRouter 链首条规则）
 * <p>
 * 多线程编排已在 {@link cn.fanstars.module.mp.service.message.MpMessageReplyOrchestrator} 中入库并设置
 * {@link MpContextHolder#isMessagePersisted()}；本 Handler 须 {@code async(false)} 同步执行并跳过重复入库。
 *
 * @author 繁星源码
 */
@Component
@Slf4j
public class MessageReceiveHandler implements WxMpMessageHandler {

    @Resource
    private MpMessageService mpMessageService;

    /**
     * 未由编排器预入库时，将消息写入 mp_message
     *
     * @return 本 Handler 不产生被动回复，恒为 null
     */
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
                                    WxMpService wxMpService, WxSessionManager sessionManager) {
        if (MpContextHolder.isMessagePersisted()) {
            // 编排器已入库，跳过
            return null;
        }
        log.info("[handle][接收到请求消息，内容：{}]", wxMessage);
        mpMessageService.receiveMessage(wxMpService, MpContextHolder.getAppId(), wxMessage);
        return null;
    }

}
