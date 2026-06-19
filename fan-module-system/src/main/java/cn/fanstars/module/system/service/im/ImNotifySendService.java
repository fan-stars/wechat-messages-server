package cn.fanstars.module.system.service.im;

import cn.fanstars.module.system.api.im.dto.ImNotifySendReqDTO;
import cn.fanstars.module.system.mq.message.im.ImNotifySendMessage;

/**
 * IM 通知发送 Service
 *
 * @author 繁星源码
 */
public interface ImNotifySendService {

    /**
     * 按模板发送 IM 通知
     *
     * @param reqDTO 发送请求
     * @return 首条发送日志编号
     */
    Long sendImNotify(ImNotifySendReqDTO reqDTO);

    /**
     * 异步消费：执行 HTTP 发送
     *
     * @param message 发送消息
     */
    void doSendImNotify(ImNotifySendMessage message);

}
