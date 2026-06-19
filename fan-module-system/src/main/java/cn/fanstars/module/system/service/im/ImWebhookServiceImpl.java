package cn.fanstars.module.system.service.im;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.fanstars.framework.common.enums.CommonStatusEnum;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.system.controller.admin.im.vo.webhook.ImWebhookPageReqVO;
import cn.fanstars.module.system.controller.admin.im.vo.webhook.ImWebhookSaveReqVO;
import cn.fanstars.module.system.dal.dataobject.im.ImWebhookDO;
import cn.fanstars.module.system.dal.mysql.im.ImWebhookMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.IM_WEBHOOK_DISABLED;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.IM_WEBHOOK_NOT_EXISTS;

/**
 * IM Webhook Service 实现类
 *
 * @author 繁星源码
 */
@Service
@Validated
public class ImWebhookServiceImpl implements ImWebhookService {

    /**
     * 凭证脱敏中间占位符，与 {@link #maskSecret(String)} 保持一致
     */
    private static final String CREDENTIAL_MASK = "****";

    @Resource
    private ImWebhookMapper imWebhookMapper;

    @Override
    public Long createImWebhook(ImWebhookSaveReqVO createReqVO) {
        ImWebhookDO webhook = BeanUtils.toBean(createReqVO, ImWebhookDO.class);
        imWebhookMapper.insert(webhook);
        return webhook.getId();
    }

    @Override
    public void updateImWebhook(ImWebhookSaveReqVO updateReqVO) {
        ImWebhookDO existing = imWebhookMapper.selectById(updateReqVO.getId());
        if (existing == null) {
            throw exception(IM_WEBHOOK_NOT_EXISTS);
        }
        ImWebhookDO updateObj = BeanUtils.toBean(updateReqVO, ImWebhookDO.class);
        preserveCredentialIfMasked(updateObj, existing);
        imWebhookMapper.updateById(updateObj);
    }

    @Override
    public void deleteImWebhook(Long id) {
        validateImWebhookExists(id);
        imWebhookMapper.deleteById(id);
    }

    @Override
    public void deleteImWebhookList(List<Long> ids) {
        imWebhookMapper.deleteByIds(ids);
    }

    @Override
    public ImWebhookDO getImWebhook(Long id) {
        return imWebhookMapper.selectById(id);
    }

    @Override
    public PageResult<ImWebhookDO> getImWebhookPage(ImWebhookPageReqVO pageReqVO) {
        return imWebhookMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ImWebhookDO> getImWebhookList() {
        return imWebhookMapper.selectList();
    }

    @Override
    public List<ImWebhookDO> getImWebhookList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return imWebhookMapper.selectByIds(ids);
    }

    @Override
    public ImWebhookDO validateImWebhookEnabled(Long id) {
        ImWebhookDO webhook = imWebhookMapper.selectById(id);
        if (webhook == null) {
            throw exception(IM_WEBHOOK_NOT_EXISTS);
        }
        if (CommonStatusEnum.isDisable(webhook.getStatus())) {
            throw exception(IM_WEBHOOK_DISABLED);
        }
        return webhook;
    }

    @VisibleForTesting
    void validateImWebhookExists(Long id) {
        if (imWebhookMapper.selectById(id) == null) {
            throw exception(IM_WEBHOOK_NOT_EXISTS);
        }
    }

    /**
     * 编辑回显为脱敏值时，未改动的凭证不应写回数据库
     */
    private static void preserveCredentialIfMasked(ImWebhookDO updateObj, ImWebhookDO existing) {
        if (shouldPreserveCredential(updateObj.getAccessToken(), existing.getAccessToken())) {
            updateObj.setAccessToken(existing.getAccessToken());
        }
        if (shouldPreserveCredential(updateObj.getSecret(), existing.getSecret())) {
            updateObj.setSecret(existing.getSecret());
        }
    }

    /**
     * 判断提交的凭证是否为脱敏占位（与 {@link #maskSecret(String)} 结果一致）
     */
    public static boolean shouldPreserveCredential(String submitted, String original) {
        if (StrUtil.isBlank(submitted) || !submitted.contains(CREDENTIAL_MASK)) {
            return false;
        }
        if (StrUtil.isBlank(original)) {
            return CREDENTIAL_MASK.equals(submitted);
        }
        return submitted.equals(maskSecret(original));
    }

    /**
     * 凭证脱敏展示
     */
    public static String maskSecret(String value) {
        if (StrUtil.isBlank(value)) {
            return value;
        }
        if (value.length() <= 8) {
            return CREDENTIAL_MASK;
        }
        return value.substring(0, 4) + CREDENTIAL_MASK + value.substring(value.length() - 4);
    }

}
