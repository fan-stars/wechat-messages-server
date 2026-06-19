package cn.fanstars.module.system.controller.admin.im.vo.webhook;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - IM Webhook 精简 Response VO")
@Data
public class ImWebhookSimpleRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "配置名称")
    private String name;

    @Schema(description = "平台")
    private Integer platform;

    @Schema(description = "状态")
    private Integer status;

}
