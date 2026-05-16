package cn.fanstars.module.mp.controller.admin.forward.log.vo;

import cn.fanstars.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.fanstars.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 转发日志分页 Request VO")
@Data
public class MessageForwardLogPageReqVO extends PageParam {

    @Schema(description = "规则编号", example = "32410")
    private Long ruleId;

    @Schema(description = "消息编号", example = "16469")
    private Long messageId;

    @Schema(description = "公众号", example = "10992")
    private Long accountId;

    @Schema(description = "OpenID", example = "29090")
    private String openid;

    @Schema(description = "转发模式")
    private Integer forwardMode;

    @Schema(description = "执行状态", example = "1")
    private Integer status;

    @Schema(description = "错误信息")
    private String errorMsg;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}