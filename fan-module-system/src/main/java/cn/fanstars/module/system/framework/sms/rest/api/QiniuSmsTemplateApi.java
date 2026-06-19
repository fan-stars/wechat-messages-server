package cn.fanstars.module.system.framework.sms.rest.api;

import cn.fanstars.module.system.framework.sms.rest.api.request.QiniuSmsTemplateSaveReqVO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

/**
 * 七牛云短信模板 REST API
 */
@HttpExchange
public interface QiniuSmsTemplateApi {

    @PostExchange(value = "/v1/template", contentType = MediaType.APPLICATION_JSON_VALUE)
    String createTemplate(@RequestBody QiniuSmsTemplateSaveReqVO body);

    @PutExchange(value = "/v1/template/{id}", contentType = MediaType.APPLICATION_JSON_VALUE)
    String updateTemplate(@PathVariable("id") String id, @RequestBody QiniuSmsTemplateSaveReqVO body);

}
