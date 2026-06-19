package cn.fanstars.module.system.api.im.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * IM 通知发送 Request DTO
 *
 * @author 繁星源码
 */
@Data
public class ImNotifySendReqDTO {

    @NotEmpty(message = "模板编码不能为空")
    private String templateCode;

    private Map<String, Object> templateParams;

    /**
     * 可选：指定 Webhook 列表；为空则使用模板绑定
     */
    private List<Long> webhookIds;

}
