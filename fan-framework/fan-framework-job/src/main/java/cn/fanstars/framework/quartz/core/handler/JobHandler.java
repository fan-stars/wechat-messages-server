package cn.fanstars.framework.quartz.core.handler;

import cn.fanstars.framework.quartz.core.handler.param.JobParamField;
import cn.fanstars.framework.quartz.core.handler.param.JobParamShape;

import java.util.Collections;
import java.util.List;

/**
 * 任务处理器
 *
 * @author 繁星源码
 */
public interface JobHandler {

    /**
     * 执行任务
     *
     * @param param 参数
     * @return 结果
     * @throws Exception 异常
     */
    String execute(String param) throws Exception;

    /**
     * 返回任务参数表单项定义，供管理端编辑表单渲染。
     * <p>
     * - {@link JobParamShape#OBJECT} / {@link JobParamShape#ARRAY}：描述单条配置内的字段；
     *   array 时每组配置复用同一套字段定义
     * - {@link JobParamShape#STRING_ARRAY}：可不重写，前端直接渲染多行字符串
     * <p>
     * 仅 GET 详情下发给前端，不入库；不需要结构化参数时不必重写
     *
     * @return 参数表单项列表，默认空
     */
    default List<JobParamField> getParamFields() {
        return Collections.emptyList();
    }

    /**
     * handlerParam 在库中的 JSON 序列化形态，与 {@link #getParamFields()} 配合使用。
     * <p>
     * 管理端：新建为单行文本不校验；编辑时按此形态校验并展示结构化表单
     */
    default JobParamShape getParamShape() {
        return JobParamShape.OBJECT;
    }

}
