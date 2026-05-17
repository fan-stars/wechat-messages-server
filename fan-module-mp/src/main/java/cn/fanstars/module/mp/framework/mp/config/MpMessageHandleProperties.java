package cn.fanstars.module.mp.framework.mp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信公众号消息处理编排配置
 * <p>
 * 配置前缀 {@code fan.mp}，如 {@code fan.mp.message-reply-wait-timeout-ms}。
 */
@ConfigurationProperties(prefix = "fan.mp")
@Data
public class MpMessageHandleProperties {

    /**
     * 主线程等待被动回复的最长时间（毫秒），超时向微信返回空串；后台任务不 cancel
     */
    private Integer messageReplyWaitTimeoutMs = 4000;

}
