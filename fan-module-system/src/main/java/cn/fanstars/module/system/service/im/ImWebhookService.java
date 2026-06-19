package cn.fanstars.module.system.service.im;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.module.system.controller.admin.im.vo.webhook.ImWebhookPageReqVO;
import cn.fanstars.module.system.controller.admin.im.vo.webhook.ImWebhookSaveReqVO;
import cn.fanstars.module.system.dal.dataobject.im.ImWebhookDO;

import java.util.Collection;
import java.util.List;

/**
 * IM Webhook Service
 *
 * @author 繁星源码
 */
public interface ImWebhookService {

    Long createImWebhook(ImWebhookSaveReqVO createReqVO);

    void updateImWebhook(ImWebhookSaveReqVO updateReqVO);

    void deleteImWebhook(Long id);

    void deleteImWebhookList(List<Long> ids);

    ImWebhookDO getImWebhook(Long id);

    PageResult<ImWebhookDO> getImWebhookPage(ImWebhookPageReqVO pageReqVO);

    List<ImWebhookDO> getImWebhookList();

    List<ImWebhookDO> getImWebhookList(Collection<Long> ids);

    /**
     * 校验 Webhook 存在且启用
     */
    ImWebhookDO validateImWebhookEnabled(Long id);

}
