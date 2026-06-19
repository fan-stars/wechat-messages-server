package cn.fanstars.module.system.framework.sms.rest.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.fanstars.framework.rest.core.HttpServiceFactory;
import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.api.SubmailSmsApi;
import cn.fanstars.module.system.framework.sms.rest.api.request.SubmailSmsSendReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.request.SubmailSmsTemplateSaveReqVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.SubmailSmsRespVO;
import cn.fanstars.module.system.framework.sms.rest.api.response.SubmailSmsTemplateQueryRespVO;
import cn.fanstars.module.system.framework.sms.rest.service.SubmailSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.support.SmsRestClientSupport;
import cn.fanstars.module.system.framework.sms.rest.support.SubmailContentConverter;
import cn.fanstars.module.system.framework.sms.rest.support.SubmailSmsPropertiesSupport;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 赛邮短信 web-rest 门面实现
 * <p>
 * 鉴权对齐官方插件：{@code appid}、{@code signature=appkey}、{@code timestamp} 随表单体出站。
 * 有模板 ID 时走 {@code message/xsend.json}，否则 {@code message/send.json}。
 */
public class SubmailSmsRestServiceImpl implements SubmailSmsRestService {

    private static final Pattern UNRESOLVED_VAR_PATTERN = Pattern.compile("@var\\(.*?\\)");

    @Getter
    private final SmsChannelProperties channelProperties;

    private final SubmailSmsApi api;

    public SubmailSmsRestServiceImpl(SmsChannelProperties channelProperties, HttpServiceFactory httpServiceFactory) {
        this.channelProperties = channelProperties;
        String baseUrl = SubmailSmsPropertiesSupport.getBaseUrl(channelProperties);
        this.api = SmsRestClientSupport.createClient(httpServiceFactory, baseUrl, (request, body, execution) ->
                execution.execute(request, body), SubmailSmsApi.class);
    }

    @Override
    public SubmailSmsTemplateQueryRespVO getTemplate(String templateId) {
        String timestamp = fetchTimestamp();
        String body = api.getTemplate(channelProperties.getApiKey(), channelProperties.getApiSecret(),
                timestamp, templateId);
        return SubmailSmsTemplateQueryRespVO.fromJson(body);
    }

    @Override
    public SubmailSmsRespVO createTemplate(SubmailSmsTemplateSaveReqVO reqVO) {
        return SubmailSmsRespVO.fromJson(api.createTemplate(toTemplateForm(reqVO, false)));
    }

    @Override
    public SubmailSmsRespVO updateTemplate(SubmailSmsTemplateSaveReqVO reqVO) {
        return SubmailSmsRespVO.fromJson(api.updateTemplate(toTemplateForm(reqVO, true)));
    }

    @Override
    public SubmailSmsRespVO sendSms(SubmailSmsSendReqVO reqVO) {
        MultiValueMap<String, String> form = buildAuthForm();
        form.add("to", reqVO.getTo());
        if (StrUtil.isNotBlank(reqVO.getTemplateId())) {
            // 模板发送走 xsend，vars 为 JSON 对象字符串
            form.add("project", reqVO.getTemplateId());
            if (reqVO.getVars() != null && !reqVO.getVars().isEmpty()) {
                form.add("vars", JSONUtil.toJsonStr(reqVO.getVars()));
            }
            return SubmailSmsRespVO.fromJson(api.xsend(form));
        }
        String content = SubmailContentConverter.toSubmailPlaceholders(reqVO.getContent());
        content = templateParamReplace(content, reqVO.getVars());
        content = SubmailSmsPropertiesSupport.normalizeSignature(
                StrUtil.blankToDefault(reqVO.getSign(), channelProperties.getSignature())) + content;
        form.add("content", content);
        return SubmailSmsRespVO.fromJson(api.send(form));
    }

    private MultiValueMap<String, String> toTemplateForm(SubmailSmsTemplateSaveReqVO reqVO, boolean update) {
        MultiValueMap<String, String> form = buildAuthForm();
        if (update && StrUtil.isNotBlank(reqVO.getTemplateId())) {
            form.add("template_id", reqVO.getTemplateId());
        }
        if (StrUtil.isNotBlank(reqVO.getTitle())) {
            form.add("sms_title", reqVO.getTitle());
        }
        if (StrUtil.isNotBlank(reqVO.getContent())) {
            form.add("sms_content", SubmailContentConverter.toSubmailPlaceholders(reqVO.getContent()));
        }
        String signature = StrUtil.blankToDefault(reqVO.getSignature(), channelProperties.getSignature());
        form.add("sms_signature", SubmailSmsPropertiesSupport.normalizeSignature(signature));
        return form;
    }

    private MultiValueMap<String, String> buildAuthForm() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("appid", channelProperties.getApiKey());
        form.add("signature", channelProperties.getApiSecret());
        form.add("timestamp", fetchTimestamp());
        return form;
    }

    private String fetchTimestamp() {
        return JSONUtil.parseObj(api.getTimestamp()).getStr("timestamp");
    }

    private static String templateParamReplace(String content, Map<String, String> templateParam) {
        if (StrUtil.isBlank(content) || templateParam == null) {
            return StrUtil.blankToDefault(content, "");
        }
        String result = content;
        for (Map.Entry<String, String> entry : templateParam.entrySet()) {
            result = result.replace("@var(" + entry.getKey() + ")",
                    StrUtil.blankToDefault(entry.getValue(), ""));
        }
        return UNRESOLVED_VAR_PATTERN.matcher(result).replaceAll("");
    }

}
