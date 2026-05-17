package cn.fanstars.module.mp.framework.mp.core.util;

import cn.hutool.core.util.StrUtil;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.util.crypto.WxMpCryptUtil;

/**
 * 微信回调被动回复 XML 包装工具
 * <p>
 * 加密模式下：下游可返回明文被动回复，或已含 {@code Encrypt} 的 AES 包装 XML（与直连微信一致）。
 * 后者须原样回传，不可再次 encrypt。
 */
public final class MpOpenReplyXmlUtils {

    /** 与 {@link cn.fanstars.module.mp.controller.admin.open.vo.MpOpenHandleMessageReqVO#ENCRYPT_TYPE_AES} 一致 */
    private static final String ENCRYPT_TYPE_AES = "aes";

    private MpOpenReplyXmlUtils() {
    }

    /**
     * 按请求加密模式包装被动回复
     *
     * @param replyXml     编排器得到的回复 XML（明文或已 AES 包装）
     * @param encryptType  请求 {@code encrypt_type}，空表示明文模式
     * @param configStorage 公众号配置（明文转 AES 时使用）
     * @return 可直接返回微信的出站 XML
     */
    public static String wrapReplyForWeChat(String replyXml, String encryptType, WxMpConfigStorage configStorage) {
        if (StrUtil.isBlank(replyXml)) {
            return "";
        }
        if (StrUtil.isBlank(encryptType)) {
            // 明文模式，直接返回 <xml><ToUserName>...
            return replyXml;
        }
        if (!ENCRYPT_TYPE_AES.equals(encryptType)) {
            // 未知加密类型，与历史行为一致
            return "";
        }
        if (isEncryptedPassiveReply(replyXml)) {
            // 下游已包装，避免二次加密导致微信无法展示
            return replyXml;
        }
        // 本地 Router 等产生的明文被动回复
        return new WxMpCryptUtil(configStorage).encrypt(replyXml);
    }

    /**
     * 是否为微信 AES 被动回复包装（含 Encrypt，且含签名/时间戳等元数据）
     *
     * @param replyXml 出站 XML
     * @return 已含 {@code <Encrypt>} 及签名元数据时为 true
     */
    public static boolean isEncryptedPassiveReply(String replyXml) {
        if (StrUtil.isBlank(replyXml)) {
            return false;
        }
        String xml = StrUtil.trim(replyXml);
        // 微信加密被动回复标准结构：<Encrypt> + <MsgSignature> + <TimeStamp> + <Nonce>
        return StrUtil.contains(xml, "<Encrypt>")
                && StrUtil.containsAny(xml, "<MsgSignature>", "<TimeStamp>", "<Nonce>");
    }

}
