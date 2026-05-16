package cn.fanstars.module.mp.controller.admin.forward.log.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 转发日志新增/修改 Request VO")
@Data
public class MessageForwardLogSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "32158")
    private Long id;

    @Schema(description = "规则编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "32410")
    @NotNull(message = "规则编号不能为空")
    private Long ruleId;

    @Schema(description = "消息编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16469")
    @NotNull(message = "消息编号不能为空")
    private Long messageId;

    @Schema(description = "公众号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10992")
    @NotNull(message = "公众号不能为空")
    private Long accountId;

    @Schema(description = "AppId", requiredMode = Schema.RequiredMode.REQUIRED, example = "21968")
    @NotEmpty(message = "AppId不能为空")
    private String appId;

    @Schema(description = "OpenID", requiredMode = Schema.RequiredMode.REQUIRED, example = "29090")
    @NotEmpty(message = "OpenID不能为空")
    private String openid;

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

    @Schema(description = "请求体")
    private String requestBody;

    @Schema(description = "响应体")
    private String responseBody;

    @Schema(description = "HTTP状态", example = "1")
    private Integer httpStatus;

    @Schema(description = "执行状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "执行状态不能为空")
    private Integer status;

    @Schema(description = "耗时")
    private Integer durationMs;

    @Schema(description = "错误信息")
    private String errorMsg;

}