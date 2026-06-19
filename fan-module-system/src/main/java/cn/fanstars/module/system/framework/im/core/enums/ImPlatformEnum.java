package cn.fanstars.module.system.framework.im.core.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.fanstars.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * IM 通知平台枚举
 *
 * @author 繁星源码
 */
@Getter
@AllArgsConstructor
public enum ImPlatformEnum implements ArrayValuable<Integer> {

    DINGTALK(1, "钉钉"),
    WECOM(2, "企业微信"),
    FEISHU(3, "飞书"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ImPlatformEnum::getPlatform).toArray(Integer[]::new);

    private final Integer platform;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static ImPlatformEnum valueOfPlatform(Integer platform) {
        return ArrayUtil.firstMatch(item -> item.getPlatform().equals(platform), values());
    }

}
