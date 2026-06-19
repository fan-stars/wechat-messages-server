package cn.fanstars.module.system.framework.sms.rest.api;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

/**
 * 阿里云短信模板审核 OpenAPI（RPC 单端点）
 */
@HttpExchange
public interface AliyunSmsTemplateApi {

    @PostExchange("/")
    String addSmsTemplate(@RequestParam Map<String, ?> params);

    @PostExchange("/")
    String modifySmsTemplate(@RequestParam Map<String, ?> params);

}
