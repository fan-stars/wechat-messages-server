package cn.fanstars.module.system.controller.admin.im.vo.log;

import cn.fanstars.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.fanstars.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - IM 发送日志分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImNotifyLogPageReqVO extends PageParam {

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "平台")
    private Integer platform;

    @Schema(description = "Webhook 编号")
    private Long webhookId;

    @Schema(description = "发送状态")
    private Integer sendStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
