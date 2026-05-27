package cn.fanstars.module.mp.controller.admin.forward.rule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 转发规则精简信息 Response VO
 */
@Schema(description = "管理后台 - 转发规则精简信息 Response VO")
@Data
public class MessageForwardRuleSimpleRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "同步转发")
    private String name;

}
