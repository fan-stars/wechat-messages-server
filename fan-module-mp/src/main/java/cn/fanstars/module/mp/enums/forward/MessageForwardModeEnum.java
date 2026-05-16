package cn.fanstars.module.mp.enums.forward;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公众号消息转发模式
 */
@Getter
@AllArgsConstructor
public enum MessageForwardModeEnum {

    SYNC(1, "同步"),
    ASYNC(2, "异步"),
    ;

    private final Integer mode;
    private final String name;

}
