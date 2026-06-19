package cn.fanstars.module.system.controller.admin.im.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - IM 模板 Response VO")
@Data
public class ImTemplateRespVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板编码")
    private String code;

    @Schema(description = "模板内容")
    private String content;

    @Schema(description = "参数列表")
    private List<String> params;

    @Schema(description = "消息类型")
    private Integer msgType;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "绑定的 Webhook 编号列表")
    private List<Long> webhookIds;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
