package cn.fanstars.module.system.framework.im.core.client;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.fanstars.framework.common.util.json.JsonUtils;
import cn.fanstars.module.system.framework.im.core.client.dto.ImWebhookSendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.nio.charset.StandardCharsets;

/**
 * IM Webhook HTTP 客户端实现
 *
 * @author 繁星源码
 */
@Component
@Slf4j
public class ImWebhookClientImpl implements ImWebhookClient {

    @Override
    public ImWebhookSendResult send(String url, String jsonBody) {
        try {
            String responseText = HttpRequest.post(url)
                    .body(jsonBody.getBytes(StandardCharsets.UTF_8))
                    .contentType("application/json;charset=UTF-8")
                    .charset(StandardCharsets.UTF_8)
                    .execute()
                    .body();
            Map<?, ?> responseObj = JsonUtils.parseObject(responseText, Map.class);
            if (responseObj == null) {
                return ImWebhookSendResult.failure("EMPTY", "平台返回为空");
            }
            // 钉钉 / 企微：errcode=0；飞书：StatusCode=0 或 code=0
            String errCode = firstNonBlank(
                    MapUtil.getStr(responseObj, "errcode"),
                    MapUtil.getStr(responseObj, "StatusCode"),
                    MapUtil.getStr(responseObj, "code"));
            String errMsg = firstNonBlank(
                    MapUtil.getStr(responseObj, "errmsg"),
                    MapUtil.getStr(responseObj, "StatusMessage"),
                    MapUtil.getStr(responseObj, "msg"));
            boolean success = Objects.equals(errCode, "0");
            return success ? ImWebhookSendResult.success(errCode, errMsg)
                    : ImWebhookSendResult.failure(errCode, errMsg);
        } catch (Exception ex) {
            log.error("[send][IM Webhook 请求异常，url={}]", url, ex);
            return ImWebhookSendResult.failure("EXCEPTION", ex.getMessage());
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                return value;
            }
        }
        return "";
    }

}
