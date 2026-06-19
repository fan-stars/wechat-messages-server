package cn.fanstars.module.system.controller.admin.im.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - IM 模板测试发送 Request VO")
@Data
public class ImTemplateSendReqVO {

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "模板编码不能为空")
    private String templateCode;

    @Schema(description = "模板参数")
    private Map<String, Object> templateParams;

    @Schema(description = "指定 Webhook 编号列表，为空则使用模板绑定")
    private List<Long> webhookIds;

}
