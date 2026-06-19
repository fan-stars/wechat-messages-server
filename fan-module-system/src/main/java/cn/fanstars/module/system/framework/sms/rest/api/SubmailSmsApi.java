package cn.fanstars.module.system.framework.sms.rest.api;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

/**
 * 赛邮短信 OpenAPI（message/*）
 */
@HttpExchange
public interface SubmailSmsApi {

    String FORM_URLENCODED_UTF8 = "application/x-www-form-urlencoded;charset=UTF-8";

    @GetExchange("/service/timestamp.json")
    String getTimestamp();

    @GetExchange("/message/template.json")
    String getTemplate(@RequestParam("appid") String appid,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("template_id") String templateId);

    @PostExchange(value = "/message/template.json", contentType = FORM_URLENCODED_UTF8)
    String createTemplate(@RequestBody MultiValueMap<String, String> form);

    @PutExchange(value = "/message/template.json", contentType = FORM_URLENCODED_UTF8)
    String updateTemplate(@RequestBody MultiValueMap<String, String> form);

    @PostExchange(value = "/message/xsend.json", contentType = FORM_URLENCODED_UTF8)
    String xsend(@RequestBody MultiValueMap<String, String> form);

    @PostExchange(value = "/message/send.json", contentType = FORM_URLENCODED_UTF8)
    String send(@RequestBody MultiValueMap<String, String> form);

}
