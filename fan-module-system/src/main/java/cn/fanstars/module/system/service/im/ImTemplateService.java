package cn.fanstars.module.system.service.im;

import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.module.system.controller.admin.im.vo.template.ImTemplatePageReqVO;
import cn.fanstars.module.system.controller.admin.im.vo.template.ImTemplateSaveReqVO;
import cn.fanstars.module.system.dal.dataobject.im.ImTemplateDO;

import java.util.List;
import java.util.Map;

/**
 * IM 模板 Service
 *
 * @author 繁星源码
 */
public interface ImTemplateService {

    Long createImTemplate(ImTemplateSaveReqVO createReqVO);

    void updateImTemplate(ImTemplateSaveReqVO updateReqVO);

    void deleteImTemplate(Long id);

    void deleteImTemplateList(List<Long> ids);

    ImTemplateDO getImTemplate(Long id);

    ImTemplateDO getImTemplateByCodeFromCache(String code);

    PageResult<ImTemplateDO> getImTemplatePage(ImTemplatePageReqVO pageReqVO);

    List<Long> getWebhookIdsByTemplateId(Long templateId);

    String formatImTemplateContent(String content, Map<String, Object> params);

}
