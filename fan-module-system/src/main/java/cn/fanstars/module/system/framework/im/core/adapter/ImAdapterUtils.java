package cn.fanstars.module.system.framework.im.core.adapter;

import cn.hutool.core.util.StrUtil;

/**
 * IM 适配器公共工具
 *
 * @author 繁星源码
 */
public final class ImAdapterUtils {

    private static final int MAX_CONTENT_LENGTH = 4000;

    private ImAdapterUtils() {
    }

    /**
     * 各平台单条消息长度上限截断
     */
    public static String truncateContent(String content) {
        if (StrUtil.length(content) <= MAX_CONTENT_LENGTH) {
            return content;
        }
        return StrUtil.sub(content, 0, MAX_CONTENT_LENGTH - 1) + "…";
    }

}
