package cn.fanstars.module.system.controller.admin.im;

import cn.fanstars.framework.common.pojo.CommonResult;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.system.api.im.dto.ImNotifySendReqDTO;
import cn.fanstars.module.system.controller.admin.im.vo.template.ImTemplatePageReqVO;
import cn.fanstars.module.system.controller.admin.im.vo.template.ImTemplateRespVO;
import cn.fanstars.module.system.controller.admin.im.vo.template.ImTemplateSaveReqVO;
import cn.fanstars.module.system.controller.admin.im.vo.template.ImTemplateSendReqVO;
import cn.fanstars.module.system.dal.dataobject.im.ImTemplateDO;
import cn.fanstars.module.system.service.im.ImNotifySendService;
import cn.fanstars.module.system.service.im.ImTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.fanstars.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 模板")
@RestController
@RequestMapping("/system/im-template")
@Validated
public class ImTemplateController {

    @Resource
    private ImTemplateService imTemplateService;
    @Resource
    private ImNotifySendService imNotifySendService;

    @PostMapping("/create")
    @Operation(summary = "创建 IM 模板")
    @PreAuthorize("@ss.hasPermission('system:im-template:create')")
    public CommonResult<Long> createImTemplate(@Valid @RequestBody ImTemplateSaveReqVO createReqVO) {
        return success(imTemplateService.createImTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 IM 模板")
    @PreAuthorize("@ss.hasPermission('system:im-template:update')")
    public CommonResult<Boolean> updateImTemplate(@Valid @RequestBody ImTemplateSaveReqVO updateReqVO) {
        imTemplateService.updateImTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 IM 模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:im-template:delete')")
    public CommonResult<Boolean> deleteImTemplate(@RequestParam("id") Long id) {
        imTemplateService.deleteImTemplate(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除 IM 模板")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:im-template:delete')")
    public CommonResult<Boolean> deleteImTemplateList(@RequestParam("ids") List<Long> ids) {
        imTemplateService.deleteImTemplateList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得 IM 模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:im-template:query')")
    public CommonResult<ImTemplateRespVO> getImTemplate(@RequestParam("id") Long id) {
        ImTemplateDO template = imTemplateService.getImTemplate(id);
        ImTemplateRespVO respVO = BeanUtils.toBean(template, ImTemplateRespVO.class);
        respVO.setWebhookIds(imTemplateService.getWebhookIdsByTemplateId(id));
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得 IM 模板分页")
    @PreAuthorize("@ss.hasPermission('system:im-template:query')")
    public CommonResult<PageResult<ImTemplateRespVO>> getImTemplatePage(@Valid ImTemplatePageReqVO pageVO) {
        PageResult<ImTemplateDO> pageResult = imTemplateService.getImTemplatePage(pageVO);
        return success(BeanUtils.toBean(pageResult, ImTemplateRespVO.class));
    }

    @PostMapping("/send-notify")
    @Operation(summary = "测试发送 IM 通知")
    @PreAuthorize("@ss.hasPermission('system:im-template:send-notify')")
    public CommonResult<Long> sendImNotify(@Valid @RequestBody ImTemplateSendReqVO sendReqVO) {
        ImNotifySendReqDTO reqDTO = new ImNotifySendReqDTO();
        reqDTO.setTemplateCode(sendReqVO.getTemplateCode());
        reqDTO.setTemplateParams(sendReqVO.getTemplateParams());
        reqDTO.setWebhookIds(sendReqVO.getWebhookIds());
        return success(imNotifySendService.sendImNotify(reqDTO));
    }

}
