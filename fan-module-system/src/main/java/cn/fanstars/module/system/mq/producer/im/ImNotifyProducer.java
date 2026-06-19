package cn.fanstars.module.system.mq.producer.im;

import cn.fanstars.module.system.mq.message.im.ImNotifySendMessage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * IM 通知消息 Producer
 *
 * @author 繁星源码
 */
@Slf4j
@Component
public class ImNotifyProducer {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 发送 {@link ImNotifySendMessage}
     */
    public void sendImNotifySendMessage(Long logId, Long webhookId, Integer msgType) {
        ImNotifySendMessage message = new ImNotifySendMessage()
                .setLogId(logId)
                .setWebhookId(webhookId)
                .setMsgType(msgType);
        applicationContext.publishEvent(message);
    }

}
