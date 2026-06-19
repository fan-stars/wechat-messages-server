package cn.fanstars.module.system.mq.consumer.im;

import cn.fanstars.module.system.mq.message.im.ImNotifySendMessage;
import cn.fanstars.module.system.service.im.ImNotifySendService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * IM 通知消息 Consumer
 *
 * @author 繁星源码
 */
@Component
@Slf4j
public class ImNotifyConsumer {

    @Resource
    private ImNotifySendService imNotifySendService;

    @EventListener
    @Async
    public void onMessage(ImNotifySendMessage message) {
        log.info("[onMessage][IM 通知消息({})]", message);
        imNotifySendService.doSendImNotify(message);
    }

}
