package cn.fanstars.module.system.framework.sms.rest.api.response;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * 赛邮短信模板查询响应
 */
@Data
public class SubmailSmsTemplateQueryRespVO {

    private String status;

    private String msg;

    private TemplateDTO template;

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(StrUtil.blankToDefault(status, ""));
    }

    @Data
    public static class TemplateDTO {

        private String templateId;

        private String title;

        private String signature;

        private String content;

        private String templateStatus;

        private String templateRejectReason;

    }

    public static SubmailSmsTemplateQueryRespVO fromJson(String body) {
        JSONObject json = JSONUtil.parseObj(extractJsonObject(body));
        SubmailSmsTemplateQueryRespVO vo = new SubmailSmsTemplateQueryRespVO();
        vo.setStatus(json.getStr("status"));
        vo.setMsg(StrUtil.blankToDefault(json.getStr("msg"), json.getStr("error")));
        JSONObject templateJson = json.getJSONObject("template");
        if (templateJson != null) {
            vo.setTemplate(parseTemplate(templateJson));
        }
        return vo;
    }

    private static TemplateDTO parseTemplate(JSONObject templateJson) {
        TemplateDTO template = new TemplateDTO();
        template.setTemplateId(templateJson.getStr("template_id"));
        template.setTitle(templateJson.getStr("sms_title"));
        template.setSignature(templateJson.getStr("sms_signature"));
        template.setContent(templateJson.getStr("sms_content"));
        template.setTemplateStatus(templateJson.getStr("template_status"));
        template.setTemplateRejectReason(templateJson.getStr("template_reject_reson"));
        return template;
    }

    private static String extractJsonObject(String body) {
        if (StrUtil.isBlank(body)) {
            return "{}";
        }
        int start = body.indexOf('{');
        int end = body.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return body.substring(start, end + 1);
        }
        return body;
    }

}
