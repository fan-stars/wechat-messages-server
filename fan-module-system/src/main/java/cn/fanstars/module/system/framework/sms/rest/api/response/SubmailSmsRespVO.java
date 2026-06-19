package cn.fanstars.module.system.framework.sms.rest.api.response;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * 赛邮短信操作响应（创建/更新/发送）
 */
@Data
public class SubmailSmsRespVO {

    private String status;

    private String msg;

    private Integer code;

    private String templateId;

    private String sendId;

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(StrUtil.blankToDefault(status, ""));
    }

    public static SubmailSmsRespVO fromJson(String body) {
        JSONObject json = JSONUtil.parseObj(extractJsonObject(body));
        SubmailSmsRespVO vo = new SubmailSmsRespVO();
        vo.setStatus(json.getStr("status"));
        vo.setMsg(StrUtil.blankToDefault(json.getStr("msg"), json.getStr("error")));
        vo.setCode(json.getInt("code"));
        vo.setTemplateId(json.getStr("template_id"));
        vo.setSendId(json.getStr("send_id"));
        return vo;
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
