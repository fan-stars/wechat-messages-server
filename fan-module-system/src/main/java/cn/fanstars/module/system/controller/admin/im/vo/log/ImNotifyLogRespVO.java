package cn.fanstars.module.system.controller.admin.im.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "管理后台 - IM 发送日志 Response VO")
@Data
public class ImNotifyLogRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "模板编号")
    private Long templateId;

    @Schema(description = "模板编码")
    private String templateCode;

    @Schema(description = "Webhook 编号")
    private Long webhookId;

    @Schema(description = "平台")
    private Integer platform;

    @Schema(description = "渲染后内容")
    private String templateContent;

    @Schema(description = "模板参数")
    private Map<String, Object> templateParams;

    @Schema(description = "发送状态")
    private Integer sendStatus;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "平台返回码")
    private String apiSendCode;

    @Schema(description = "平台返回提示")
    private String apiSendMsg;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
