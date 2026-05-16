package cn.fanstars.module.mp.controller.admin.forward.log.vo;

import cn.fanstars.framework.excel.core.annotations.DictFormat;
import cn.fanstars.framework.excel.core.convert.DictConvert;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 转发日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MessageForwardLogRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "32158")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "规则编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "32410")
    @ExcelProperty("规则编号")
    private Long ruleId;

    @Schema(description = "消息编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "16469")
    @ExcelProperty("消息编号")
    private Long messageId;

    @Schema(description = "公众号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10992")
    @ExcelProperty("公众号")
    private Long accountId;

    @Schema(description = "AppId", requiredMode = Schema.RequiredMode.REQUIRED, example = "21968")
    @ExcelProperty("AppId")
    private String appId;

    @Schema(description = "OpenID", requiredMode = Schema.RequiredMode.REQUIRED, example = "29090")
    @ExcelProperty("OpenID")
    private String openid;

    @Schema(description = "转发模式", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "转发模式", converter = DictConvert.class)
    @DictFormat("mp_message_forward_mode")
    private Integer forwardMode;

    @Schema(description = "接收响应", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean receiveResponse;

    @Schema(description = "响应回复", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean useResponseAsReply;

    @Schema(description = "目标地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String targetUrl;

    @Schema(description = "请求体")
    private String requestBody;

    @Schema(description = "响应体")
    private String responseBody;

    @Schema(description = "HTTP状态", example = "1")
    @ExcelProperty("HTTP状态")
    private Integer httpStatus;

    @Schema(description = "执行状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "执行状态", converter = DictConvert.class)
    @DictFormat("mp_message_forward_log_status")
    private Integer status;

    @Schema(description = "耗时")
    @ExcelProperty("耗时")
    private Integer durationMs;

    @Schema(description = "错误信息")
    @ExcelProperty("错误信息")
    private String errorMsg;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
