package cn.fanstars.module.system.framework.im.core.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.fanstars.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * IM 通知逻辑消息类型
 *
 * @author 繁星源码
 */
@Getter
@AllArgsConstructor
public enum ImMsgTypeEnum implements ArrayValuable<Integer> {

    TEXT(1, "文本"),
    MARKDOWN(2, "Markdown"),
    ;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ImMsgTypeEnum::getType).toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static ImMsgTypeEnum valueOfType(Integer type) {
        return ArrayUtil.firstMatch(item -> item.getType().equals(type), values());
    }

}
