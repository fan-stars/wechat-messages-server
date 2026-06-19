package cn.fanstars.module.system.api.im;

import cn.fanstars.module.system.api.im.dto.ImNotifySendReqDTO;
import cn.fanstars.module.system.service.im.ImNotifySendService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * IM 通知发送 API 实现类
 *
 * @author 繁星源码
 */
@Service
@Validated
public class ImNotifySendApiImpl implements ImNotifySendApi {

    @Resource
    private ImNotifySendService imNotifySendService;

    @Override
    public Long send(ImNotifySendReqDTO reqDTO) {
        return imNotifySendService.sendImNotify(reqDTO);
    }

}
