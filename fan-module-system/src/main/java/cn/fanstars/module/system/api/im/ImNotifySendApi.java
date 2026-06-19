package cn.fanstars.module.system.api.im;

import cn.fanstars.module.system.api.im.dto.ImNotifySendReqDTO;
import jakarta.validation.Valid;

/**
 * IM 通知发送 API
 *
 * @author 繁星源码
 */
public interface ImNotifySendApi {

    /**
     * 按模板编码发送 IM 通知
     *
     * @param reqDTO 发送请求
     * @return 首条发送日志编号
     */
    Long send(@Valid ImNotifySendReqDTO reqDTO);

}
