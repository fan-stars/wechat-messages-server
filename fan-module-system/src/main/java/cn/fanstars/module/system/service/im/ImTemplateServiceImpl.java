package cn.fanstars.module.system.service.im;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.system.controller.admin.im.vo.template.ImTemplatePageReqVO;
import cn.fanstars.module.system.controller.admin.im.vo.template.ImTemplateSaveReqVO;
import cn.fanstars.module.system.dal.dataobject.im.ImTemplateDO;
import cn.fanstars.module.system.dal.dataobject.im.ImTemplateWebhookDO;
import cn.fanstars.module.system.dal.mysql.im.ImTemplateMapper;
import cn.fanstars.module.system.dal.mysql.im.ImTemplateWebhookMapper;
import cn.fanstars.module.system.dal.redis.RedisKeyConstants;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.IM_TEMPLATE_CODE_DUPLICATE;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.IM_TEMPLATE_NOT_EXISTS;

/**
 * IM 模板 Service 实现类
 *
 * @author 繁星源码
 */
@Service
@Validated
public class ImTemplateServiceImpl implements ImTemplateService {

    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    @Resource
    private ImTemplateMapper imTemplateMapper;
    @Resource
    private ImTemplateWebhookMapper imTemplateWebhookMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createImTemplate(ImTemplateSaveReqVO createReqVO) {
        validateImTemplateCodeDuplicate(null, createReqVO.getCode());
        ImTemplateDO template = BeanUtils.toBean(createReqVO, ImTemplateDO.class);
        template.setParams(parseTemplateContentParams(template.getContent()));
        imTemplateMapper.insert(template);
        saveTemplateWebhooks(template.getId(), createReqVO.getWebhookIds());
        return template.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = RedisKeyConstants.IM_TEMPLATE, allEntries = true)
    public void updateImTemplate(ImTemplateSaveReqVO updateReqVO) {
        validateImTemplateExists(updateReqVO.getId());
        validateImTemplateCodeDuplicate(updateReqVO.getId(), updateReqVO.getCode());
        ImTemplateDO updateObj = BeanUtils.toBean(updateReqVO, ImTemplateDO.class);
        updateObj.setParams(parseTemplateContentParams(updateObj.getContent()));
        imTemplateMapper.updateById(updateObj);
        imTemplateWebhookMapper.deleteByTemplateId(updateReqVO.getId());
        saveTemplateWebhooks(updateReqVO.getId(), updateReqVO.getWebhookIds());
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.IM_TEMPLATE, allEntries = true)
    public void deleteImTemplate(Long id) {
        validateImTemplateExists(id);
        imTemplateMapper.deleteById(id);
        imTemplateWebhookMapper.deleteByTemplateId(id);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.IM_TEMPLATE, allEntries = true)
    public void deleteImTemplateList(List<Long> ids) {
        imTemplateMapper.deleteByIds(ids);
        ids.forEach(imTemplateWebhookMapper::deleteByTemplateId);
    }

    @Override
    public ImTemplateDO getImTemplate(Long id) {
        return imTemplateMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheNames = RedisKeyConstants.IM_TEMPLATE, key = "#code", unless = "#result == null")
    public ImTemplateDO getImTemplateByCodeFromCache(String code) {
        return imTemplateMapper.selectByCode(code);
    }

    @Override
    public PageResult<ImTemplateDO> getImTemplatePage(ImTemplatePageReqVO pageReqVO) {
        return imTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    public List<Long> getWebhookIdsByTemplateId(Long templateId) {
        return imTemplateWebhookMapper.selectWebhookIdsByTemplateId(templateId);
    }

    @Override
    public String formatImTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }

    private void saveTemplateWebhooks(Long templateId, List<Long> webhookIds) {
        if (CollUtil.isEmpty(webhookIds)) {
            return;
        }
        webhookIds.forEach(webhookId -> imTemplateWebhookMapper.insert(ImTemplateWebhookDO.builder()
                .templateId(templateId)
                .webhookId(webhookId)
                .build()));
    }

    @VisibleForTesting
    List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

    @VisibleForTesting
    void validateImTemplateExists(Long id) {
        if (imTemplateMapper.selectById(id) == null) {
            throw exception(IM_TEMPLATE_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    void validateImTemplateCodeDuplicate(Long id, String code) {
        ImTemplateDO template = imTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        if (id == null || !template.getId().equals(id)) {
            throw exception(IM_TEMPLATE_CODE_DUPLICATE, code);
        }
    }

}
