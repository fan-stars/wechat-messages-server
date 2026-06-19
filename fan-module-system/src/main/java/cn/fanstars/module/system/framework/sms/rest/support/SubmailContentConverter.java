package cn.fanstars.module.system.framework.sms.rest.support;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

import java.util.regex.Pattern;

/**
 * 赛邮模板正文占位符与繁星 {@code {key}} 互转（仅赛邮适配层使用）
 */
public final class SubmailContentConverter {

    private static final Pattern PATTERN_BRACE_PARAMS = Pattern.compile("\\{(.*?)}");

    private static final Pattern PATTERN_SUBMAIL_VAR_PARAMS = Pattern.compile("@var\\(([^)]+)\\)");

    private SubmailContentConverter() {
    }

    /**
     * 繁星 {@code {key}} → 赛邮 {@code @var(key)}
     */
    public static String toSubmailPlaceholders(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        return ReUtil.replaceAll(content, PATTERN_BRACE_PARAMS, "@var($1)");
    }

    /**
     * 赛邮 {@code @var(key)} → 繁星 {@code {key}}
     */
    public static String fromSubmailPlaceholders(String content) {
        if (StrUtil.isBlank(content)) {
            return content;
        }
        return ReUtil.replaceAll(content, PATTERN_SUBMAIL_VAR_PARAMS, "{$1}");
    }

}
