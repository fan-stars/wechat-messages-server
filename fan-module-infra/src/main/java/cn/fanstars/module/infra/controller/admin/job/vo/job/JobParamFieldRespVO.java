package cn.fanstars.module.infra.controller.admin.job.vo.job;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 定时任务处理器参数字段 Response VO")
@Data
public class JobParamFieldRespVO {

    @Schema(description = "参数键名", example = "retainDay")
    private String key;

    @Schema(description = "表单项标签", example = "保留天数")
    private String label;

    @Schema(description = "默认值", example = "14")
    private Object defaultValue;

    @Schema(description = "是否必填", example = "true")
    private Boolean required;

}
