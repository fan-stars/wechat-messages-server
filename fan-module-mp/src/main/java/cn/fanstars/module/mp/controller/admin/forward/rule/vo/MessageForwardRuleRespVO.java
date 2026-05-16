package cn.fanstars.module.mp.controller.admin.forward.rule.vo;

import cn.fanstars.framework.excel.core.annotations.DictFormat;
import cn.fanstars.framework.excel.core.convert.DictConvert;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 转发规则 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MessageForwardRuleRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "25034")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "公众号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20712")
    @ExcelProperty("公众号")
    private Long accountId;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("规则名称")
    private String name;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("common_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer status;

    @Schema(description = "优先级", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("优先级")
    private Integer priority;

    @Schema(description = "转发模式", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "转发模式", converter = DictConvert.class)
    @DictFormat("mp_message_forward_mode") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer forwardMode;

    @Schema(description = "接收响应", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "接收响应", converter = DictConvert.class)
    @DictFormat("infra_boolean_string") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Boolean receiveResponse;

    @Schema(description = "响应回复", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "响应回复", converter = DictConvert.class)
    @DictFormat("infra_boolean_string") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Boolean useResponseAsReply;

    @Schema(description = "目标地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "http://127.0.0.1:48080/admin-api/mp/open/wxXXX")
    @ExcelProperty("目标地址")
    private String targetUrl;

    @Schema(description = "超时", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("超时")
    private Integer timeoutMs;

    @Schema(description = "消息类型")
    @ExcelProperty("消息类型")
    private String messageTypes;

    @Schema(description = "记录日志", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "记录日志", converter = DictConvert.class)
    @DictFormat("infra_boolean_string") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Boolean enableLog;

    @Schema(description = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}