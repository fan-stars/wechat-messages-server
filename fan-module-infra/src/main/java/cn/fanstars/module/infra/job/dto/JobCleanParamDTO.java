package cn.fanstars.module.infra.job.dto;

import lombok.Data;

/**
 * 日志/任务清理类 Job 的通用 JSON 参数
 */
@Data
public class JobCleanParamDTO {

    /**
     * 保留天数
     */
    private Integer retainDay;

    /**
     * 每批删除条数
     */
    private Integer deleteLimit;

}
