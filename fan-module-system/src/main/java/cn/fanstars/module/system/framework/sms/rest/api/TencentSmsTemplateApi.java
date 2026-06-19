package cn.fanstars.module.system.framework.sms.rest.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * 腾讯云短信模板审核 OpenAPI（RPC 单端点）
 */
@HttpExchange
public interface TencentSmsTemplateApi {

    @PostExchange(value = "/", contentType = MediaType.APPLICATION_JSON_VALUE)
    String addSmsTemplate(@RequestBody String bodyJson);

    @PostExchange(value = "/", contentType = MediaType.APPLICATION_JSON_VALUE)
    String modifySmsTemplate(@RequestBody String bodyJson);

}
