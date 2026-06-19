package cn.fanstars.module.system.enums.im;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * IM 通知发送状态
 *
 * @author 繁星源码
 */
@Getter
@AllArgsConstructor
public enum ImSendStatusEnum {

    INIT(0),
    SUCCESS(10),
    FAILURE(20),
    IGNORE(30),
    ;

    private final int status;

}
