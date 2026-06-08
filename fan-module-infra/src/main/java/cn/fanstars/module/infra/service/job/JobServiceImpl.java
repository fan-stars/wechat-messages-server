package cn.fanstars.module.infra.service.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.json.JsonUtils;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.framework.quartz.core.handler.JobHandler;
import cn.fanstars.framework.quartz.core.handler.param.JobParamField;
import cn.fanstars.framework.quartz.core.handler.param.JobParamShape;
import cn.fanstars.framework.quartz.core.scheduler.SchedulerManager;
import cn.fanstars.framework.quartz.core.util.CronUtils;
import cn.fanstars.module.infra.controller.admin.job.vo.job.JobPageReqVO;
import cn.fanstars.module.infra.controller.admin.job.vo.job.JobParamFieldRespVO;
import cn.fanstars.module.infra.controller.admin.job.vo.job.JobRespVO;
import cn.fanstars.module.infra.controller.admin.job.vo.job.JobSaveReqVO;
import cn.fanstars.module.infra.dal.dataobject.job.JobDO;
import cn.fanstars.module.infra.dal.mysql.job.JobMapper;
import cn.fanstars.module.infra.enums.job.JobStatusEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.framework.common.util.collection.CollectionUtils.containsAny;
import static cn.fanstars.module.infra.enums.ErrorCodeConstants.*;

/**
 * 定时任务 Service 实现类
 *
 * @author 繁星源码
 */
@Service
@Validated
@Slf4j
public class JobServiceImpl implements JobService {

    @Resource
    private JobMapper jobMapper;

    @Resource
    private SchedulerManager schedulerManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createJob(JobSaveReqVO createReqVO) throws SchedulerException {
        validateCronExpression(createReqVO.getCronExpression());
        // 1.1 校验唯一性
        if (jobMapper.selectByHandlerName(createReqVO.getHandlerName()) != null) {
            throw exception(JOB_HANDLER_EXISTS);
        }
        // 1.2 校验 JobHandler 是否存在
        validateJobHandlerExists(createReqVO.getHandlerName());
        // 1.3 校验参数（新建不强制结构化 JSON）
        validateHandlerParamOnCreate(createReqVO.getHandlerName(), createReqVO.getHandlerParam());

        // 2. 插入 JobDO
        JobDO job = BeanUtils.toBean(createReqVO, JobDO.class);
        job.setStatus(JobStatusEnum.INIT.getStatus());
        fillJobMonitorTimeoutEmpty(job);
        jobMapper.insert(job);

        // 3.1 添加 Job 到 Quartz 中
        schedulerManager.addJob(job.getId(), job.getHandlerName(), job.getHandlerParam(), job.getCronExpression(),
                createReqVO.getRetryCount(), createReqVO.getRetryInterval());
        // 3.2 更新 JobDO
        JobDO updateObj = JobDO.builder().id(job.getId()).status(JobStatusEnum.NORMAL.getStatus()).build();
        jobMapper.updateById(updateObj);
        return job.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJob(JobSaveReqVO updateReqVO) throws SchedulerException {
        validateCronExpression(updateReqVO.getCronExpression());
        // 1.1 校验存在
        JobDO job = validateJobExists(updateReqVO.getId());
        // 1.2 只有开启状态，才可以修改.原因是，如果出暂停状态，修改 Quartz Job 时，会导致任务又开始执行
        if (!job.getStatus().equals(JobStatusEnum.NORMAL.getStatus())) {
            throw exception(JOB_UPDATE_ONLY_NORMAL_STATUS);
        }
        // 1.3 校验 JobHandler 是否存在
        validateJobHandlerExists(updateReqVO.getHandlerName());
        // 1.4 校验参数（编辑时按 Handler 的 paramShape 校验 JSON）
        validateHandlerParamOnUpdate(updateReqVO.getHandlerName(), updateReqVO.getHandlerParam());

        // 2. 更新 JobDO
        JobDO updateObj = BeanUtils.toBean(updateReqVO, JobDO.class);
        fillJobMonitorTimeoutEmpty(updateObj);
        jobMapper.updateById(updateObj);

        // 3. 更新 Job 到 Quartz 中
        schedulerManager.updateJob(job.getHandlerName(), updateReqVO.getHandlerParam(), updateReqVO.getCronExpression(),
                updateReqVO.getRetryCount(), updateReqVO.getRetryInterval());
    }

    private void validateJobHandlerExists(String handlerName) {
        try {
            Object handler = SpringUtil.getBean(handlerName);
            assert handler != null;
            if (!(handler instanceof JobHandler)) {
                throw exception(JOB_HANDLER_BEAN_TYPE_ERROR);
            }
        } catch (NoSuchBeanDefinitionException e) {
            throw exception(JOB_HANDLER_BEAN_NOT_EXISTS);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJobStatus(Long id, Integer status) throws SchedulerException {
        // 校验 status
        if (!containsAny(status, JobStatusEnum.NORMAL.getStatus(), JobStatusEnum.STOP.getStatus())) {
            throw exception(JOB_CHANGE_STATUS_INVALID);
        }
        // 校验存在
        JobDO job = validateJobExists(id);
        // 校验是否已经为当前状态
        if (job.getStatus().equals(status)) {
            throw exception(JOB_CHANGE_STATUS_EQUALS);
        }
        // 更新 Job 状态
        JobDO updateObj = JobDO.builder().id(id).status(status).build();
        jobMapper.updateById(updateObj);

        // 更新状态 Job 到 Quartz 中
        if (JobStatusEnum.NORMAL.getStatus().equals(status)) { // 开启
            schedulerManager.resumeJob(job.getHandlerName());
        } else { // 暂停
            schedulerManager.pauseJob(job.getHandlerName());
        }
    }

    @Override
    public void triggerJob(Long id) throws SchedulerException {
        // 校验存在
        JobDO job = validateJobExists(id);

        // 触发 Quartz 中的 Job
        schedulerManager.triggerJob(job.getId(), job.getHandlerName(), job.getHandlerParam());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncJob() throws SchedulerException {
        // 1. 查询 Job 配置
        List<JobDO> jobList = jobMapper.selectList();

        // 2. 遍历处理
        for (JobDO job : jobList) {
            // 2.1 先删除，再创建
            schedulerManager.deleteJob(job.getHandlerName());
            schedulerManager.addJob(job.getId(), job.getHandlerName(), job.getHandlerParam(), job.getCronExpression(),
                    job.getRetryCount(), job.getRetryInterval());
            // 2.2 如果 status 为暂停，则需要暂停
            if (Objects.equals(job.getStatus(), JobStatusEnum.STOP.getStatus())) {
                schedulerManager.pauseJob(job.getHandlerName());
            }
            log.info("[syncJob][id({}) handlerName({}) 同步完成]", job.getId(), job.getHandlerName());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJob(Long id) throws SchedulerException {
        // 校验存在
        JobDO job = validateJobExists(id);
        // 更新
        jobMapper.deleteById(id);

        // 删除 Job 到 Quartz 中
        schedulerManager.deleteJob(job.getHandlerName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteJobList(List<Long> ids) throws SchedulerException {
        // 批量删除
        List<JobDO> jobs = jobMapper.selectByIds(ids);
        jobMapper.deleteByIds(ids);

        // 删除 Job 到 Quartz 中
        for (JobDO job : jobs) {
            schedulerManager.deleteJob(job.getHandlerName());
        }
    }

    private JobDO validateJobExists(Long id) {
        JobDO job = jobMapper.selectById(id);
        if (job == null) {
            throw exception(JOB_NOT_EXISTS);
        }
        return job;
    }

    private void validateCronExpression(String cronExpression) {
        if (!CronUtils.isValid(cronExpression)) {
            throw exception(JOB_CRON_EXPRESSION_VALID);
        }
    }

    /**
     * 新建任务参数校验
     * 保持原始单行输入，不强制 JSON（与前端新建 UI 一致）
     */
    private void validateHandlerParamOnCreate(String handlerName, String handlerParam) {
    }

    /**
     * 编辑任务：按 JobHandler#getParamShape() 分支校验 handlerParam
     * <p>
     * - 无字段定义且 object 形态：跳过
     * - object：JSON 对象 + 必填字段
     * - array：JSON 对象数组，每条校验必填字段
     * - string_array：JSON 字符串数组，元素非空
     */
    private void validateHandlerParamOnUpdate(String handlerName, String handlerParam) {
        JobHandler jobHandler = getJobHandler(handlerName);
        List<JobParamField> paramFields = jobHandler.getParamFields();
        if (paramFields == null) {
            paramFields = Collections.emptyList();
        }
        JobParamShape paramShape = jobHandler.getParamShape();
        if (paramShape == null) {
            paramShape = JobParamShape.OBJECT;
        }
        if (paramShape == JobParamShape.OBJECT && CollUtil.isEmpty(paramFields)) {
            return;
        }
        if (StrUtil.isBlank(handlerParam)) {
            throw exception(JOB_HANDLER_PARAM_INVALID);
        }
        switch (paramShape) {
            case ARRAY -> validateHandlerParamArray(handlerParam, paramFields);
            case STRING_ARRAY -> validateHandlerParamStringArray(handlerParam);
            default -> validateHandlerParamObject(handlerParam, paramFields);
        }
    }

    /**
     * object 形态校验
     * 示例：{@code {"retainDay":14}}
     */
    private void validateHandlerParamObject(String handlerParam, List<JobParamField> paramFields) {
        if (!JsonUtils.isJsonObject(handlerParam)) {
            throw exception(JOB_HANDLER_PARAM_INVALID);
        }
        Map<String, Object> paramMap = JSONObject.parseObject(handlerParam, new TypeReference<>() {
        });
        validateRequiredFields(paramMap, paramFields);
    }

    /**
     * string_array 形态校验
     * 示例：{@code ["a.com","b.com"]}，允许空数组
     */
    private void validateHandlerParamStringArray(String handlerParam) {
        if (!JsonUtils.isJsonArray(handlerParam)) {
            throw exception(JOB_HANDLER_PARAM_INVALID);
        }
        JSONArray array = JSONArray.parseArray(handlerParam);
        if (array == null) {
            throw exception(JOB_HANDLER_PARAM_INVALID);
        }
        for (Object element : array) {
            if (!(element instanceof String) || StrUtil.isBlank((String) element)) {
                throw exception(JOB_HANDLER_PARAM_INVALID);
            }
        }
    }

    /**
     * array 形态校验
     * 示例：{@code [{...},{...}]}，每条配置校验必填字段
     */
    @SuppressWarnings("unchecked")
    private void validateHandlerParamArray(String handlerParam, List<JobParamField> paramFields) {
        if (CollUtil.isEmpty(paramFields)) {
            throw exception(JOB_HANDLER_PARAM_INVALID);
        }
        if (!JsonUtils.isJsonArray(handlerParam)) {
            throw exception(JOB_HANDLER_PARAM_INVALID);
        }
        JSONArray array = JSONArray.parseArray(handlerParam);
        if (array == null) {
            throw exception(JOB_HANDLER_PARAM_INVALID);
        }
        for (Object element : array) {
            if (!(element instanceof Map)) {
                throw exception(JOB_HANDLER_PARAM_INVALID);
            }
            validateRequiredFields((Map<String, Object>) element, paramFields);
        }
    }

    /**
     * 校验 object / array 单条配置中的必填字段
     */
    private void validateRequiredFields(Map<String, Object> paramMap, List<JobParamField> paramFields) {
        for (JobParamField field : paramFields) {
            if (!Boolean.TRUE.equals(field.getRequired())) {
                continue;
            }
            Object value = paramMap.get(field.getKey());
            if (value == null || (value instanceof String && StrUtil.isBlank((String) value))) {
                throw exception(JOB_HANDLER_PARAM_INVALID);
            }
        }
    }

    /**
     * 任务详情
     * 除 DO 字段外，附带 paramFields + paramShape 供编辑表单结构化渲染（不入库）
     */
    @Override
    public JobRespVO getJobDetail(Long id) {
        JobDO job = validateJobExists(id);
        JobRespVO respVO = BeanUtils.toBean(job, JobRespVO.class);
        JobHandler jobHandler = getJobHandler(job.getHandlerName());
        respVO.setParamFields(BeanUtils.toBean(
                jobHandler.getParamFields() != null ? jobHandler.getParamFields() : Collections.emptyList(),
                JobParamFieldRespVO.class));
        JobParamShape paramShape = jobHandler.getParamShape();
        // 与前端约定小写枚举名：object / array / string_array
        respVO.setParamShape(paramShape != null ? paramShape.name().toLowerCase() : JobParamShape.OBJECT.name().toLowerCase());
        return respVO;
    }

    private JobHandler getJobHandler(String handlerName) {
        validateJobHandlerExists(handlerName);
        return SpringUtil.getBean(handlerName, JobHandler.class);
    }

    @Override
    public JobDO getJob(Long id) {
        return jobMapper.selectById(id);
    }

    @Override
    public PageResult<JobDO> getJobPage(JobPageReqVO pageReqVO) {
        return jobMapper.selectPage(pageReqVO);
    }

    private static void fillJobMonitorTimeoutEmpty(JobDO job) {
        if (job.getMonitorTimeout() == null) {
            job.setMonitorTimeout(0);
        }
    }

}
