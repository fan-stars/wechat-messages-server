package cn.fanstars.module.mp.service.forward.bo;

import lombok.Data;

/**
 * 消息转发执行结果
 */
@Data
public class MessageForwardExecuteResultBO {

    /**
     * 采纳的微信被动回复 XML（原样透传下游响应）
     */
    private String replyXml;

    public static MessageForwardExecuteResultBO empty() {
        return new MessageForwardExecuteResultBO();
    }

    public static MessageForwardExecuteResultBO ofReply(String replyXml) {
        MessageForwardExecuteResultBO result = new MessageForwardExecuteResultBO();
        result.setReplyXml(replyXml);
        return result;
    }

}
