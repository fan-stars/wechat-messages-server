package cn.fanstars.module.system.framework.sms.rest.api.response;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * 腾讯云短信模板审核响应（AddSmsTemplate / ModifySmsTemplate）
 */
@Data
public class TencentSmsTemplateRespVO {

    private TencentErrorDTO error;

    private String templateId;

    @Data
    public static class TencentErrorDTO {
        private String message;
    }

    public boolean hasError() {
        return error != null;
    }

    /**
     * 从原始 JSON 响应解析
     */
    public static TencentSmsTemplateRespVO fromJson(String body) {
        JSONObject json = JSONUtil.parseObj(body);
        JSONObject response = json.getJSONObject("Response");
        TencentSmsTemplateRespVO vo = new TencentSmsTemplateRespVO();
        JSONObject errorJson = response.getJSONObject("Error");
        if (errorJson != null) {
            TencentErrorDTO error = new TencentErrorDTO();
            error.setMessage(errorJson.getStr("Message"));
            vo.setError(error);
            return vo;
        }
        JSONObject status = response.getJSONObject("AddTemplateStatus");
        if (status == null) {
            status = response.getJSONObject("ModifyTemplateStatus");
        }
        if (status != null) {
            vo.setTemplateId(status.getStr("TemplateId"));
        }
        return vo;
    }

}
