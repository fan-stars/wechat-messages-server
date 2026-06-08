package cn.fanstars.module.infra.job.util;

import cn.fanstars.framework.quartz.core.handler.param.JobParamField;

import java.util.Arrays;
import java.util.List;

/**
 * 日志/任务清理类 Job 的参数表单项
 * object 形态，默认 paramShape=OBJECT
 */
public final class JobCleanParamFields {

    private JobCleanParamFields() {
    }

    public static List<JobParamField> build() {
        return Arrays.asList(
                new JobParamField("retainDay", "保留天数", 14, true),
                new JobParamField("deleteLimit", "每批删除条数", 100, true)
        );
    }

}
