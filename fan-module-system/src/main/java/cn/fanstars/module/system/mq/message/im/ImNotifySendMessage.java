package cn.fanstars.module.system.mq.message.im;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * IM 通知发送消息
 *
 * @author 繁星源码
 */
@Data
public class ImNotifySendMessage {

    @NotNull(message = "发送日志编号不能为空")
    private Long logId;

    @NotNull(message = "Webhook 编号不能为空")
    private Long webhookId;

    @NotNull(message = "消息类型不能为空")
    private Integer msgType;

}
