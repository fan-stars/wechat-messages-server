package cn.fanstars.module.mp.controller.admin.open;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.fanstars.framework.tenant.core.aop.TenantIgnore;
import cn.fanstars.framework.tenant.core.util.TenantUtils;
import cn.fanstars.module.mp.controller.admin.open.vo.MpOpenCheckSignatureReqVO;
import cn.fanstars.module.mp.controller.admin.open.vo.MpOpenHandleMessageReqVO;
import cn.fanstars.module.mp.dal.dataobject.account.MpAccountDO;
import cn.fanstars.module.mp.framework.mp.core.MpServiceFactory;
import cn.fanstars.module.mp.framework.mp.core.context.MpContextHolder;
import cn.fanstars.module.mp.framework.mp.core.util.MpOpenReplyXmlUtils;
import cn.fanstars.module.mp.service.account.MpAccountService;
import cn.fanstars.module.mp.service.message.MpMessageReplyOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Objects;

/**
 * 微信公众号服务器回调入口（验签、收消息、被动回复）
 */
@Tag(name = "管理后台 - 公众号回调")
@RestController
@RequestMapping("/mp/open")
@Validated
@Slf4j
public class MpOpenController {

    @Resource
    private MpServiceFactory mpServiceFactory;

    @Resource
    private MpAccountService mpAccountService;

    @Resource
    private MpMessageReplyOrchestrator mpMessageReplyOrchestrator;

    /**
     * 接收微信公众号的消息推送
     *
     * <p>验签、解析入站 XML 后委托 {@link MpMessageReplyOrchestrator} 编排，再按加密模式包装出站 XML。
     *
     * @see <a href="https://developers.weixin.qq.com/doc/offiaccount/Message_Management/Receiving_standard_messages.html">接收普通消息</a>
     * @param appId 公众号 appId（路径变量）
     * @param content 微信 POST 原始 XML
     * @param reqVO 回调 query 参数
     * @return 被动回复 XML 或空串
     */
    @Operation(summary = "处理消息")
    @PostMapping(value = "/{appId}", produces = "application/xml; charset=UTF-8")
    @TenantIgnore
    public String handleMessage(@PathVariable("appId") String appId,
                                @RequestBody String content,
                                MpOpenHandleMessageReqVO reqVO) {
        log.info("[handleMessage][appId({}) 推送消息，参数({}) 内容({})]", appId, reqVO, content);

        // 由 appId 反查租户
        MpAccountDO account = mpAccountService.getAccountFromCache(appId);
        Assert.notNull(account, "公众号 appId({}) 不存在", appId);
        try {
            // 供后续 Service / Mapper 使用
            MpContextHolder.setAppId(appId);
            // 在租户上下文中执行业务
            return TenantUtils.execute(account.getTenantId(),
                    () -> handleMessage0(appId, content, reqVO));
        } finally {
            // 避免 Tomcat 线程复用泄漏
            MpContextHolder.clear();
        }
    }

    /**
     * 接收微信公众号的校验签名（GET 接入验证）
     *
     * @see <a href="https://developers.weixin.qq.com/doc/offiaccount/Basic_Information/Access_Overview.html">接入概述</a>
     * @param appId 公众号 appId
     * @param reqVO 含 signature、timestamp、nonce、echostr
     * @return 验签通过时回显 echostr，否则返回非法提示
     */
    @Operation(summary = "校验签名")
    @GetMapping(value = "/{appId}", produces = "text/plain;charset=utf-8")
    @TenantIgnore
    public String checkSignature(@PathVariable("appId") String appId,
                                 MpOpenCheckSignatureReqVO reqVO) {
        log.info("[checkSignature][appId({}) 接收到来自微信服务器的认证消息({})]", appId, reqVO);
        WxMpService wxMpService = mpServiceFactory.getRequiredMpService(appId);
        if (wxMpService.checkSignature(reqVO.getTimestamp(), reqVO.getNonce(), reqVO.getSignature())) {
            return reqVO.getEchostr();
        }
        return "非法请求";
    }

    /**
     * 验签、解析入站消息后编排被动回复，并按 {@code encrypt_type} 包装出站 XML
     */
    private String handleMessage0(String appId, String content, MpOpenHandleMessageReqVO reqVO) {
        WxMpService mppService = mpServiceFactory.getRequiredMpService(appId);
        // 校验微信 GET/POST 共用 token 的 signature（与 encrypt 无关）
        Assert.isTrue(mppService.checkSignature(reqVO.getTimestamp(), reqVO.getNonce(), reqVO.getSignature()),
                "非法请求");

        // 第一步：解密/解析入站 XML → WxMpXmlMessage
        WxMpXmlMessage inMessage = null;
        if (StrUtil.isBlank(reqVO.getEncrypt_type())) {
            // 明文模式
            inMessage = WxMpXmlMessage.fromXml(content);
        } else if (Objects.equals(reqVO.getEncrypt_type(), MpOpenHandleMessageReqVO.ENCRYPT_TYPE_AES)) {
            // 安全模式（AES）
            inMessage = WxMpXmlMessage.fromEncryptedXml(content, mppService.getWxMpConfigStorage(),
                    reqVO.getTimestamp(), reqVO.getNonce(), reqVO.getMsg_signature());
        }
        Assert.notNull(inMessage, "消息解析失败，原因：消息为空");

        // 第二步：编排（入库 ∥ 同步转发 ∥ 本地 Router），返回被动回复 XML 或空串
        MpAccountDO account = mpAccountService.getAccountFromCache(appId);
        String replyXml = mpMessageReplyOrchestrator.orchestrate(appId, account, inMessage, content, reqVO, mppService);
        if (StrUtil.isBlank(replyXml)) {
            // 无被动回复，微信仍视为处理成功
            return "";
        }

        // 第三步：按 encrypt_type 包装出站 XML
        if (Objects.equals(reqVO.getEncrypt_type(), MpOpenHandleMessageReqVO.ENCRYPT_TYPE_AES)
                && MpOpenReplyXmlUtils.isEncryptedPassiveReply(replyXml)) {
            // 下游已返回 <Encrypt> 包装，不可再 encrypt
            log.info("[handleMessage][appId({}) 下游已返回 AES 被动回复，原样返回微信]", appId);
            return replyXml;
        }
        // 本地 Router 明文 → encrypt；明文模式 → 原样
        return MpOpenReplyXmlUtils.wrapReplyForWeChat(replyXml, reqVO.getEncrypt_type(),
                mppService.getWxMpConfigStorage());
    }

}
