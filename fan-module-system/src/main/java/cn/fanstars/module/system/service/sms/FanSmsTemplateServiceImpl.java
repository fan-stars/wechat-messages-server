package cn.fanstars.module.system.service.sms;

import cn.hutool.core.util.StrUtil;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.system.controller.admin.sms.vo.template.SmsTemplateSaveReqVO;
import cn.fanstars.module.system.dal.dataobject.sms.SmsChannelDO;
import cn.fanstars.module.system.dal.dataobject.sms.SmsTemplateDO;
import cn.fanstars.module.system.dal.mysql.sms.SmsTemplateMapper;
import cn.fanstars.module.system.dal.redis.RedisKeyConstants;
import cn.fanstars.module.system.framework.sms.core.client.dto.SmsTemplateRespDTO;
import cn.fanstars.module.system.framework.sms.core.enums.SmsTemplateAuditStatusEnum;
import cn.fanstars.module.system.framework.sms.rest.SmsTemplateAuditClient;
import cn.fanstars.module.system.framework.sms.rest.SmsTemplateAuditClientFactory;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateModifyReqDTO;
import cn.fanstars.module.system.framework.sms.rest.dto.SmsTemplateSubmitReqDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.*;

/**
 * 短信模板 Service Fan 实现：支持云平台审核提交、修改重审与状态同步
 */
@Service
@Primary
@Slf4j
public class FanSmsTemplateServiceImpl extends SmsTemplateServiceImpl implements FanSmsTemplateService {

    @Resource
    private SmsTemplateMapper smsTemplateMapper;
    @Resource
    private SmsTemplateAuditClientFactory auditClientFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisKeyConstants.SMS_TEMPLATE, allEntries = true)
    public Long createSmsTemplate(SmsTemplateSaveReqVO createReqVO) {
        SmsChannelDO channelDO = validateSmsChannel(createReqVO.getChannelId());
        validateSmsTemplateCodeDuplicate(null, createReqVO.getCode());

        boolean submitAudit = Boolean.TRUE.equals(createReqVO.getSubmitAudit());
        String apiTemplateId = createReqVO.getApiTemplateId();

        SmsTemplateDO template = BeanUtils.toBean(createReqVO, SmsTemplateDO.class);
        template.setParams(parseTemplateContentParams(template.getContent()));
        template.setChannelCode(channelDO.getCode());

        if (submitAudit && StrUtil.isBlank(apiTemplateId)) {
            SmsTemplateAuditClient auditClient = auditClientFactory.getClient(channelDO.getId());
            SmsTemplateRespDTO resp = invokeSubmit(auditClient, createReqVO);
            applyAuditResult(template, resp);
        } else if (StrUtil.isNotBlank(apiTemplateId)) {
            validateApiTemplate(createReqVO.getChannelId(), apiTemplateId);
            template.setApiTemplateId(apiTemplateId);
            template.setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus());
            template.setAuditSyncTime(LocalDateTime.now());
        } else {
            throw exception(SMS_TEMPLATE_API_TEMPLATE_ID_REQUIRED);
        }

        smsTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisKeyConstants.SMS_TEMPLATE, allEntries = true)
    public void updateSmsTemplate(SmsTemplateSaveReqVO updateReqVO) {
        SmsTemplateDO oldTemplate = smsTemplateMapper.selectById(updateReqVO.getId());
        if (oldTemplate == null) {
            throw exception(SMS_TEMPLATE_NOT_EXISTS);
        }
        SmsChannelDO channelDO = validateSmsChannel(updateReqVO.getChannelId());
        validateSmsTemplateCodeDuplicate(updateReqVO.getId(), updateReqVO.getCode());

        boolean submitAudit = Boolean.TRUE.equals(updateReqVO.getSubmitAudit());
        boolean auditContentChanged = isAuditContentChanged(oldTemplate, updateReqVO);

        SmsTemplateDO updateObj = BeanUtils.toBean(updateReqVO, SmsTemplateDO.class);
        updateObj.setParams(parseTemplateContentParams(updateObj.getContent()));
        updateObj.setChannelCode(channelDO.getCode());

        String apiTemplateId = StrUtil.blankToDefault(updateReqVO.getApiTemplateId(), oldTemplate.getApiTemplateId());
        if (auditContentChanged) {
            if (submitAudit || StrUtil.isNotBlank(apiTemplateId)) {
                SmsTemplateAuditClient auditClient = auditClientFactory.getClient(channelDO.getId());
                SmsTemplateRespDTO resp;
                if (StrUtil.isNotBlank(apiTemplateId)) {
                    resp = invokeModify(auditClient, updateReqVO, apiTemplateId);
                } else {
                    resp = invokeSubmit(auditClient, updateReqVO);
                }
                applyAuditResult(updateObj, resp);
            } else if (StrUtil.isNotBlank(updateReqVO.getApiTemplateId())) {
                validateApiTemplate(updateReqVO.getChannelId(), updateReqVO.getApiTemplateId());
                updateObj.setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus());
                updateObj.setAuditSyncTime(LocalDateTime.now());
            } else {
                throw exception(SMS_TEMPLATE_API_TEMPLATE_ID_REQUIRED);
            }
        } else if (StrUtil.isNotBlank(updateReqVO.getApiTemplateId())
                && !Objects.equals(updateReqVO.getApiTemplateId(), oldTemplate.getApiTemplateId())) {
            validateApiTemplate(updateReqVO.getChannelId(), updateReqVO.getApiTemplateId());
            updateObj.setAuditStatus(SmsTemplateAuditStatusEnum.SUCCESS.getStatus());
            updateObj.setAuditSyncTime(LocalDateTime.now());
        } else {
            updateObj.setAuditStatus(oldTemplate.getAuditStatus());
            updateObj.setAuditReason(oldTemplate.getAuditReason());
            updateObj.setAuditSyncTime(oldTemplate.getAuditSyncTime());
            updateObj.setApiTemplateId(apiTemplateId);
        }

        smsTemplateMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisKeyConstants.SMS_TEMPLATE, allEntries = true)
    public void submitAudit(Long id) {
        SmsTemplateDO template = smsTemplateMapper.selectById(id);
        if (template == null) {
            throw exception(SMS_TEMPLATE_NOT_EXISTS);
        }
        SmsTemplateAuditClient auditClient = auditClientFactory.getClient(template.getChannelId());
        SmsTemplateSubmitReqDTO req = new SmsTemplateSubmitReqDTO();
        req.setCode(template.getCode());
        req.setName(template.getName());
        req.setContent(template.getContent());
        req.setType(template.getType());
        req.setRemark(template.getRemark());
        SmsTemplateRespDTO resp = invokeSubmit(auditClient, req);
        applyAuditResult(template, resp);
        smsTemplateMapper.updateById(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisKeyConstants.SMS_TEMPLATE, allEntries = true)
    public SmsTemplateDO syncAuditStatus(Long id) {
        SmsTemplateDO template = smsTemplateMapper.selectById(id);
        if (template == null) {
            throw exception(SMS_TEMPLATE_NOT_EXISTS);
        }
        if (StrUtil.isBlank(template.getApiTemplateId())) {
            throw exception(SMS_TEMPLATE_AUDIT_NOT_SUBMITTED);
        }
        syncAndUpdate(template);
        return template;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisKeyConstants.SMS_TEMPLATE, allEntries = true)
    public void syncAuditStatusList(List<Long> ids) {
        for (Long id : ids) {
            syncAuditStatus(id);
        }
    }

    private void syncAndUpdate(SmsTemplateDO template) {
        try {
            SmsTemplateAuditClient auditClient = auditClientFactory.getClient(template.getChannelId());
            SmsTemplateRespDTO resp = auditClient.syncTemplate(template.getApiTemplateId());
            if (resp == null) {
                throw exception(SMS_TEMPLATE_API_NOT_FOUND);
            }
            template.setAuditStatus(resp.getAuditStatus());
            template.setAuditReason(resp.getAuditReason());
            template.setAuditSyncTime(LocalDateTime.now());
            smsTemplateMapper.updateById(template);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw exception(SMS_TEMPLATE_API_ERROR, ex.getMessage());
        }
    }

    private static boolean isAuditContentChanged(SmsTemplateDO oldTemplate, SmsTemplateSaveReqVO updateReqVO) {
        return !Objects.equals(oldTemplate.getContent(), updateReqVO.getContent())
                || !Objects.equals(oldTemplate.getType(), updateReqVO.getType());
    }

    private static SmsTemplateRespDTO invokeSubmit(SmsTemplateAuditClient auditClient, SmsTemplateSaveReqVO reqVO) {
        return invokeSubmit(auditClient, toSubmitReq(reqVO));
    }

    private static SmsTemplateRespDTO invokeSubmit(SmsTemplateAuditClient auditClient, SmsTemplateSubmitReqDTO req) {
        try {
            return auditClient.submitTemplate(req);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw exception(SMS_TEMPLATE_SUBMIT_AUDIT_FAIL, ex.getMessage());
        }
    }

    private static SmsTemplateRespDTO invokeModify(SmsTemplateAuditClient auditClient,
                                                   SmsTemplateSaveReqVO reqVO, String apiTemplateId) {
        SmsTemplateModifyReqDTO req = new SmsTemplateModifyReqDTO();
        req.setApiTemplateId(apiTemplateId);
        req.setName(reqVO.getName());
        req.setContent(reqVO.getContent());
        req.setType(reqVO.getType());
        req.setRemark(reqVO.getRemark());
        try {
            return auditClient.modifyTemplate(req);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw exception(SMS_TEMPLATE_SUBMIT_AUDIT_FAIL, ex.getMessage());
        }
    }

    private static SmsTemplateSubmitReqDTO toSubmitReq(SmsTemplateSaveReqVO reqVO) {
        SmsTemplateSubmitReqDTO req = new SmsTemplateSubmitReqDTO();
        req.setCode(reqVO.getCode());
        req.setName(reqVO.getName());
        req.setContent(reqVO.getContent());
        req.setType(reqVO.getType());
        req.setRemark(reqVO.getRemark());
        return req;
    }

    private static void applyAuditResult(SmsTemplateDO template, SmsTemplateRespDTO resp) {
        template.setApiTemplateId(resp.getId());
        template.setAuditStatus(resp.getAuditStatus());
        template.setAuditReason(resp.getAuditReason());
        template.setAuditSyncTime(LocalDateTime.now());
    }

}
