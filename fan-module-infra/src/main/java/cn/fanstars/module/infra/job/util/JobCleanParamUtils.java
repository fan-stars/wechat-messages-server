package cn.fanstars.module.infra.job.util;

import cn.fanstars.framework.common.util.json.JsonUtils;
import cn.fanstars.module.infra.job.dto.JobCleanParamDTO;
import cn.hutool.core.util.StrUtil;

/**
 * 清理类定时任务参数解析
 */
public final class JobCleanParamUtils {

    private JobCleanParamUtils() {
    }

    /**
     * 解析 object 形态 handlerParam
     * 缺省或解析失败时使用默认值
     */
    public static JobCleanParamDTO resolveRetainDayAndDeleteLimit(
            String param, int defaultRetainDay, int defaultDeleteLimit) {
        JobCleanParamDTO result = new JobCleanParamDTO();
        result.setRetainDay(defaultRetainDay);
        result.setDeleteLimit(defaultDeleteLimit);
        if (StrUtil.isBlank(param)) {
            return result;
        }
        JobCleanParamDTO parsed = JsonUtils.parseObject(param, JobCleanParamDTO.class);
        if (parsed == null) {
            return result;
        }
        if (parsed.getRetainDay() != null) {
            result.setRetainDay(parsed.getRetainDay());
        }
        if (parsed.getDeleteLimit() != null) {
            result.setDeleteLimit(parsed.getDeleteLimit());
        }
        return result;
    }

}
