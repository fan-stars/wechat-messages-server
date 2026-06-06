package cn.fanstars.framework.quartz.core.handler.param;

import cn.fanstars.framework.quartz.core.handler.JobHandler;

/**
 * 定时任务 handlerParam 序列化形态
 * <p>
 * 与 {@link JobHandler#getParamFields()} 一起，
 * 由 infra 模块在编辑时下发给前端（paramShape 为小写枚举名：object / array / string_array）
 */
public enum JobParamShape {

    /**
     * JSON 对象
     * 示例：{"retainDay":14}
     */
    OBJECT,

    /**
     * JSON 对象数组
     * 示例：[{"host":"x","port":21}]
     */
    ARRAY,

    /**
     * JSON 字符串数组
     * 示例：["a.com","b.com"]
     */
    STRING_ARRAY

}
