package cn.fanstars.module.system.controller.admin.sms;

import cn.fanstars.framework.common.pojo.CommonResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.system.controller.admin.sms.vo.template.SmsTemplateRespVO;
import cn.fanstars.module.system.dal.dataobject.sms.SmsTemplateDO;
import cn.fanstars.module.system.service.sms.FanSmsTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.fanstars.framework.common.pojo.CommonResult.success;

/**
 * 短信模板审核扩展接口（Fan）
 */
@Tag(name = "管理后台 - 短信模板审核")
@RestController
@RequestMapping("/system/sms-template")
@Validated
public class FanSmsTemplateController {

    @Resource
    private FanSmsTemplateService fanSmsTemplateService;

    @PostMapping("/submit-audit")
    @Operation(summary = "提交短信模板审核")
    @Parameter(name = "id", description = "模板编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:sms-template:submit-audit')")
    public CommonResult<Boolean> submitAudit(@RequestParam("id") Long id) {
        fanSmsTemplateService.submitAudit(id);
        return success(true);
    }

    @PostMapping("/sync-audit-status")
    @Operation(summary = "同步短信模板审核状态")
    @Parameter(name = "id", description = "模板编号", required = true)
    @PreAuthorize("@ss.hasPermission('system:sms-template:sync-audit-status')")
    public CommonResult<SmsTemplateRespVO> syncAuditStatus(@RequestParam("id") Long id) {
        SmsTemplateDO template = fanSmsTemplateService.syncAuditStatus(id);
        return success(BeanUtils.toBean(template, SmsTemplateRespVO.class));
    }

    @PostMapping("/sync-audit-status-list")
    @Operation(summary = "批量同步短信模板审核状态")
    @Parameter(name = "ids", description = "模板编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('system:sms-template:sync-audit-status')")
    public CommonResult<Boolean> syncAuditStatusList(@RequestParam("ids") List<Long> ids) {
        fanSmsTemplateService.syncAuditStatusList(ids);
        return success(true);
    }

}
