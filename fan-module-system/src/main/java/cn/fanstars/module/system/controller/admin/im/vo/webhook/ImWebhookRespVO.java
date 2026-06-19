package cn.fanstars.module.system.controller.admin.im.vo.webhook;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - IM Webhook Response VO")
@Data
public class ImWebhookRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "配置名称")
    private String name;

    @Schema(description = "平台")
    private Integer platform;

    @Schema(description = "access_token / key")
    private String accessToken;

    @Schema(description = "安全密钥：钉钉加签 / 飞书签名校验时填写，其他情况可空")
    private String secret;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
