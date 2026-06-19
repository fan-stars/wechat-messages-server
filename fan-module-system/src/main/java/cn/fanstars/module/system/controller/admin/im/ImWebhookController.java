package cn.fanstars.module.system.controller.admin.im;

import cn.fanstars.framework.common.pojo.CommonResult;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.system.controller.admin.im.vo.webhook.ImWebhookPageReqVO;
import cn.fanstars.module.system.controller.admin.im.vo.webhook.ImWebhookRespVO;
import cn.fanstars.module.system.controller.admin.im.vo.webhook.ImWebhookSaveReqVO;
import cn.fanstars.module.system.controller.admin.im.vo.webhook.ImWebhookSimpleRespVO;
import cn.fanstars.module.system.dal.dataobject.im.ImWebhookDO;
import cn.fanstars.module.system.service.im.ImWebhookService;
import cn.fanstars.module.system.service.im.ImWebhookServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static cn.fanstars.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM Webhook")
@RestController
@RequestMapping("/system/im-webhook")
@Validated
public class ImWebhookController {

    @Resource
    private ImWebhookService imWebhookService;

    @PostMapping("/create")
    @Operation(summary = "创建 IM Webhook")
    @PreAuthorize("@ss.hasPermission('system:im-webhook:create')")
    public CommonResult<Long> createImWebhook(@Valid @RequestBody ImWebhookSaveReqVO createReqVO) {
        return success(imWebhookService.createImWebhook(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 IM Webhook")
    @PreAuthorize("@ss.hasPermission('system:im-webhook:update')")
    public CommonResult<Boolean> updateImWebhook(@Valid @RequestBody ImWebhookSaveReqVO updateReqVO) {
        imWebhookService.updateImWebhook(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 IM Webhook")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:im-webhook:delete')")
    public CommonResult<Boolean> deleteImWebhook(@RequestParam("id") Long id) {
        imWebhookService.deleteImWebhook(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Operation(summary = "批量删除 IM Webhook")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:im-webhook:delete')")
    public CommonResult<Boolean> deleteImWebhookList(@RequestParam("ids") List<Long> ids) {
        imWebhookService.deleteImWebhookList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得 IM Webhook")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:im-webhook:query')")
    public CommonResult<ImWebhookRespVO> getImWebhook(@RequestParam("id") Long id) {
        ImWebhookDO webhook = imWebhookService.getImWebhook(id);
        ImWebhookRespVO respVO = BeanUtils.toBean(webhook, ImWebhookRespVO.class);
        maskWebhookSecrets(respVO);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得 IM Webhook 分页")
    @PreAuthorize("@ss.hasPermission('system:im-webhook:query')")
    public CommonResult<PageResult<ImWebhookRespVO>> getImWebhookPage(@Valid ImWebhookPageReqVO pageVO) {
        PageResult<ImWebhookDO> pageResult = imWebhookService.getImWebhookPage(pageVO);
        PageResult<ImWebhookRespVO> result = BeanUtils.toBean(pageResult, ImWebhookRespVO.class);
        result.getList().forEach(this::maskWebhookSecrets);
        return success(result);
    }

    @GetMapping({"/list-all-simple", "/simple-list"})
    @Operation(summary = "获得 IM Webhook 精简列表")
    public CommonResult<List<ImWebhookSimpleRespVO>> getSimpleImWebhookList() {
        List<ImWebhookDO> list = imWebhookService.getImWebhookList();
        list.sort(Comparator.comparing(ImWebhookDO::getId));
        return success(BeanUtils.toBean(list, ImWebhookSimpleRespVO.class));
    }

    private void maskWebhookSecrets(ImWebhookRespVO respVO) {
        if (respVO == null) {
            return;
        }
        respVO.setAccessToken(ImWebhookServiceImpl.maskSecret(respVO.getAccessToken()));
        respVO.setSecret(ImWebhookServiceImpl.maskSecret(respVO.getSecret()));
    }

}
