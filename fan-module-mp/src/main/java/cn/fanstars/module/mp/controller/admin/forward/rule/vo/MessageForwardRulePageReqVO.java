package cn.fanstars.module.mp.controller.admin.forward.rule.vo;

import cn.fanstars.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.fanstars.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 转发规则分页 Request VO")
@Data
public class MessageForwardRulePageReqVO extends PageParam {

    @Schema(description = "公众号", example = "20712")
    private Long accountId;

    @Schema(description = "规则名称", example = "赵六")
    private String name;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "转发模式")
    private Integer forwardMode;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}