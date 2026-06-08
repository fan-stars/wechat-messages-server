package cn.fanstars.module.infra.job.logger;

import cn.fanstars.framework.quartz.core.handler.JobHandler;
import cn.fanstars.framework.quartz.core.handler.param.JobParamField;
import cn.fanstars.framework.tenant.core.aop.TenantIgnore;
import cn.fanstars.module.infra.job.dto.JobCleanParamDTO;
import cn.fanstars.module.infra.job.job.JobLogCleanJob;
import cn.fanstars.module.infra.job.util.JobCleanParamFields;
import cn.fanstars.module.infra.job.util.JobCleanParamUtils;
import cn.fanstars.module.infra.service.logger.ApiErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

import java.util.List;

/**
 * 物理删除 N 天前的错误日志的 Job
 *
 * @author j-sentinel
 */
@Slf4j
@Component
public class ErrorLogCleanJob implements JobHandler {

    @Resource
    private ApiErrorLogService apiErrorLogService;

    /**
     * 清理超过（14）天的日志
     */
    private static final Integer JOB_CLEAN_RETAIN_DAY = 14;

    /**
     * 每次删除间隔的条数，如果值太高可能会造成数据库的压力过大
     */
    private static final Integer DELETE_LIMIT = 100;

    /**
     * object 形态
     * 与 {@link JobLogCleanJob} 共用字段定义
     */
    @Override
    public List<JobParamField> getParamFields() {
        return JobCleanParamFields.build();
    }

    @Override
    @TenantIgnore
    public String execute(String param) {
        JobCleanParamDTO config = JobCleanParamUtils.resolveRetainDayAndDeleteLimit(
                param, JOB_CLEAN_RETAIN_DAY, DELETE_LIMIT);
        Integer count = apiErrorLogService.cleanErrorLog(config.getRetainDay(), config.getDeleteLimit());
        log.info("[execute][定时执行清理错误日志数量 ({}) 个]", count);
        return String.format("定时执行清理错误日志数量 %s 个", count);
    }

}
