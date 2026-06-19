package cn.fanstars.module.system.controller.admin.im.vo.template;

import cn.fanstars.framework.common.enums.CommonStatusEnum;
import cn.fanstars.framework.common.validation.InEnum;
import cn.fanstars.module.system.framework.im.core.enums.ImMsgTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - IM 模板创建/修改 Request VO")
@Data
public class ImTemplateSaveReqVO {

    @Schema(description = "编号")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "模板名称不能为空")
    private String name;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "模板编码不能为空")
    private String code;

    @Schema(description = "模板内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "模板内容不能为空")
    private String content;

    @Schema(description = "消息类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "消息类型不能为空")
    @InEnum(ImMsgTypeEnum.class)
    private Integer msgType;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "状态不能为空")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "绑定的 Webhook 编号列表")
    private List<Long> webhookIds;

}
