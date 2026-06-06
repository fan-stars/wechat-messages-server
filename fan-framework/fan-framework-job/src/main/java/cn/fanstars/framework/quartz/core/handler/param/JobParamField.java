package cn.fanstars.framework.quartz.core.handler.param;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 定时任务处理器参数表单项定义
 * 描述单条配置内的字段元数据
 * <p>
 * 写入 handlerParam JSON 的键名为 {@link #key}
 * 前端根据 {@link #defaultValue} 类型选择 Input / InputNumber
 */
@Data
@NoArgsConstructor
public class JobParamField {

    /**
     * 参数键名
     * 序列化进 handlerParam JSON 对象
     */
    private String key;

    /**
     * 表单项标签
     */
    private String label;

    /**
     * 默认值（无存量参数时使用）
     * 数值类型前端渲染为 InputNumber，否则为 Input
     */
    private Object defaultValue;

    /**
     * 是否必填
     */
    private Boolean required;

    public JobParamField(String key, String label, Object defaultValue) {
        this(key, label, defaultValue, false);
    }

    public JobParamField(String key, String label, Object defaultValue, Boolean required) {
        this.key = key;
        this.label = label;
        this.defaultValue = defaultValue;
        this.required = required;
    }

}
