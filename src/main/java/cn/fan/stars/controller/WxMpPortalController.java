package cn.fan.stars.controller;

import cn.fan.stars.config.MsgPushConfig;
import cn.fan.stars.util.HttpUtils;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 微信消息认证
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/mp/portal/{appid}")
public class WxMpPortalController {

    private final WxMpService wxService;
    private final WxMpMessageRouter messageRouter;
    private final MsgPushConfig msgPushConfig;
    private final static Map<String, List<MsgPushConfig.MsgPushDTO>> pushInfoMaps = new HashMap<>();

    @PostConstruct
    public void init() {
        List<MsgPushConfig.MsgPushDTO> pushInfos = msgPushConfig.getPushInfos();
        if (pushInfos == null || pushInfos.isEmpty()) {
            return;
        }
        Map<String, List<MsgPushConfig.MsgPushDTO>> tempPushInfoMaps = pushInfos.stream()
                .collect(Collectors.groupingBy(MsgPushConfig.MsgPushDTO::getAppId,
                        Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
                                .sorted(Comparator.comparingInt(MsgPushConfig.MsgPushDTO::getPriority))
                                .collect(Collectors.toList()))
                ));
        pushInfoMaps.putAll(tempPushInfoMaps);
        log.info("{}", JSON.toJSONString(pushInfoMaps));
    }

    /**
     * 微信消息配置认证
     *
     * @param appid     APPID
     * @param signature signature
     * @param timestamp timestamp
     * @param nonce     nonce
     * @param echostr   echostr
     * @return String
     */
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@PathVariable String appid,
                          @RequestParam(name = "signature", required = false) String signature,
                          @RequestParam(name = "timestamp", required = false) String timestamp,
                          @RequestParam(name = "nonce", required = false) String nonce,
                          @RequestParam(name = "echostr", required = false) String echostr) {

        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
                timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        if (wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "非法请求";
    }

    /**
     * 微信消息接收
     *
     * @param appid        APPID
     * @param requestBody  requestBody
     * @param signature    signature
     * @param timestamp    timestamp
     * @param nonce        nonce
     * @param openid       openid
     * @param encType      encType
     * @param msgSignature msgSignature
     * @return String
     */
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String appid,
                       @RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam("openid") String openid,
                       @RequestParam(name = "encrypt_type", required = false) String encType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        log.info("\n接收微信请求：[openid=[{}], [signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                openid, signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        if (!wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        List<String> outs = new ArrayList<>();
        try {
            String requestUrlParams = "?signature=" + signature + "&timestamp=" + timestamp + "&nonce=" + nonce
                    + "&openid=" + openid + "&encrypt_type=" + encType + "&msg_signature=" + msgSignature;
            msgPush(appid, requestUrlParams, requestBody, outs);
        } catch (Exception e) {
            log.info("msg push is error: ", e);
        }

        String out = null;
        if (!outs.isEmpty()) {
            out = outs.get(0);
        }
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (out == null) {
                if (outMessage == null) {
                    return "";
                }
                out = outMessage.toXml();
            }
        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(requestBody, wxService.getWxMpConfigStorage(),
                    timestamp, nonce, msgSignature);
            log.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (out == null) {
                if (outMessage == null) {
                    return "";
                }
                out = outMessage.toEncryptedXml(wxService.getWxMpConfigStorage());
            }
        }

        log.debug("\n组装回复信息：{}", out);
        return out;
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.messageRouter.route(message);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }
        return null;
    }

    private void msgPush(String appid, String requestUrlParams, String requestBody, List<String> outs) {
        List<MsgPushConfig.MsgPushDTO> msgPushDTOS = pushInfoMaps.get(appid);
        if (msgPushDTOS == null) {
            log.info("appid: {} message is not have push config", appid);
            return;
        }
        for (MsgPushConfig.MsgPushDTO msgPushDTO : msgPushDTOS) {
            String url = msgPushDTO.getPushUrl() + requestUrlParams;
            try {
                String result = HttpUtils.post(url, requestBody);
                log.info("msg push appid: {} url: {} result: {}", appid, url, result);
                if (StringUtils.isNotBlank(result)) {
                    outs.add(result);
                }
            } catch (Exception e) {
                log.error("msg push is error: ", e);
            }
        }
    }

}
