package cn.fanstars.module.system.framework.sms.rest.api.response;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * 七牛云短信模板创建/更新响应
 */
@Data
public class QiniuSmsTemplateRespVO {

    private String id;

    private String error;

    private String message;

    public boolean hasError() {
        return ObjectUtil.isNotEmpty(error);
    }

    /**
     * 从原始 JSON 响应解析
     */
    public static QiniuSmsTemplateRespVO fromJson(String body) {
        JSONObject json = JSONUtil.parseObj(body);
        QiniuSmsTemplateRespVO vo = new QiniuSmsTemplateRespVO();
        vo.setId(json.getStr("id"));
        vo.setError(json.getStr("error"));
        vo.setMessage(json.getStr("message"));
        return vo;
    }

}
