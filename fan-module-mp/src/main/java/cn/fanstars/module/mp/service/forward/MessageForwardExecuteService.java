package cn.fanstars.module.mp.service.forward;

import cn.fanstars.module.mp.controller.admin.open.vo.MpOpenHandleMessageReqVO;
import cn.fanstars.module.mp.dal.dataobject.account.MpAccountDO;
import cn.fanstars.module.mp.service.forward.bo.MessageForwardExecuteResultBO;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

/**
 * 公众号消息转发执行 Service
 */
public interface MessageForwardExecuteService {

    /**
     * 按规则透传微信请求到下游
     *
     * @param account    公众号账号
     * @param inMessage  已解析的入站消息（规则匹配）
     * @param rawContent 微信原始 XML
     * @param reqVO      微信 query 参数
     * @param messageId  已入库消息编号
     * @return 执行结果（含被动回复 XML）
     */
    MessageForwardExecuteResultBO execute(MpAccountDO account, WxMpXmlMessage inMessage,
                                          String rawContent, MpOpenHandleMessageReqVO reqVO,
                                          Long messageId);

}
