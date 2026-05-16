package cn.fanstars.module.mp.enums.forward;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公众号消息转发日志状态
 */
@Getter
@AllArgsConstructor
public enum MessageForwardLogStatusEnum {

    SUCCESS(0, "成功"),
    FAILURE(1, "失败"),
    TIMEOUT(2, "超时"),
    SKIPPED(3, "跳过"),
    ;

    private final Integer status;
    private final String name;

}
