package cn.fanstars.module.mp.framework.rest.service;

import cn.fanstars.module.mp.framework.rest.bo.MpMessageForwardHttpResultBO;
import cn.fanstars.module.mp.service.forward.MessageForwardExecuteServiceImpl;

import java.util.Map;

/**
 * 公众号消息透传转发 HTTP 客户端
 * <p>
 * 封装框架 {@code RestClient}，对 {@link MessageForwardExecuteServiceImpl} 屏蔽底层调用细节。
 */
public interface MpMessageForwardClient {

    /**
     * POST 透传 XML 到目标地址
     *
     * @param url        完整目标 URL（含微信验签 query）
     * @param headers    请求头
     * @param rawContent 微信原始 XML
     * @param timeoutMs  本次请求超时（毫秒），按转发规则配置
     */
    MpMessageForwardHttpResultBO forward(String url, Map<String, String> headers, String rawContent, int timeoutMs);

}
