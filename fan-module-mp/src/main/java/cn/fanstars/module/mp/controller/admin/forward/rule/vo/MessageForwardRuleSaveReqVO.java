package cn.fanstars.module.mp.controller.admin.forward.rule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 转发规则新增/修改 Request VO")
@Data
public class MessageForwardRuleSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "25034")
    private Long id;

    @Schema(description = "公众号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20712")
    @NotNull(message = "公众号不能为空")
    private Long accountId;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "规则名称不能为空")
    private String name;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "优先级", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "优先级不能为空")
    private Integer priority;

    @Schema(description = "转发模式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "转发模式不能为空")
    private Integer forwardMode;

    @Schema(description = "接收响应", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "接收响应不能为空")
    private Boolean receiveResponse;

    @Schema(description = "响应回复", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "响应回复不能为空")
    private Boolean useResponseAsReply;

    @Schema(description = "目标地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @NotEmpty(message = "目标地址不能为空")
    private String targetUrl;

    @Schema(description = "超时", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "超时不能为空")
    private Integer timeoutMs;

    @Schema(description = "消息类型")
    private String messageTypes;

    @Schema(description = "记录日志", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "记录日志不能为空")
    private Boolean enableLog;

    @Schema(description = "备注", example = "随便")
    private String remark;

}