package cn.fanstars.module.system.framework.sms.rest.support;

import cn.hutool.core.util.StrUtil;
import cn.fanstars.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;

/**
 * 赛邮短信渠道配置辅助
 */
public final class SubmailSmsPropertiesSupport {

    public static final String DEFAULT_BASE_URL = "http://api.mysubmail.com/";

    private SubmailSmsPropertiesSupport() {
    }

    /**
     * API Host 优先取渠道 {@code callbackUrl}，为空时使用赛邮默认域名
     */
    public static String getBaseUrl(SmsChannelProperties properties) {
        if (properties != null && StrUtil.isNotBlank(properties.getCallbackUrl())) {
            return properties.getCallbackUrl().endsWith("/")
                    ? properties.getCallbackUrl() : properties.getCallbackUrl() + "/";
        }
        return DEFAULT_BASE_URL;
    }

    /**
     * 赛邮 template_status → 本地 {@link SmsTemplateAuditStatusEnum}
     * <p>
     * 0 未提交、1 审核中、2 通过、3 驳回
     */
    public static Integer convertAuditStatus(String templateStatus) {
        if (StrUtil.isBlank(templateStatus)) {
            return SmsTemplateAuditStatusEnum.CHECKING.getStatus();
        }
        return switch (templateStatus) {
            case "2" -> SmsTemplateAuditStatusEnum.SUCCESS.getStatus();
            case "3" -> SmsTemplateAuditStatusEnum.FAIL.getStatus();
            case "0", "1" -> SmsTemplateAuditStatusEnum.CHECKING.getStatus();
            default -> SmsTemplateAuditStatusEnum.CHECKING.getStatus();
        };
    }

    /**
     * 签名统一包裹为【】，与赛邮控制台约定一致
     */
    public static String normalizeSignature(String sign) {
        if (StrUtil.isBlank(sign)) {
            return "";
        }
        String normalized = sign.replace("【", "").replace("】", "");
        return "【" + normalized + "】";
    }

}
