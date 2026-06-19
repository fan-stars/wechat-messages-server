package cn.fanstars.module.system.controller.admin.im.vo.webhook;

import cn.fanstars.framework.common.enums.CommonStatusEnum;
import cn.fanstars.framework.common.validation.InEnum;
import cn.fanstars.module.system.framework.im.core.enums.ImPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - IM Webhook 创建/修改 Request VO")
@Data
public class ImWebhookSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "配置名称不能为空")
    private String name;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "平台不能为空")
    @InEnum(ImPlatformEnum.class)
    private Integer platform;

    @Schema(description = "access_token / key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "access_token 不能为空")
    private String accessToken;

    @Schema(description = "安全密钥：钉钉加签 / 飞书签名校验时填写，其他情况可空")
    private String secret;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

    @Schema(description = "备注")
    private String remark;

}
