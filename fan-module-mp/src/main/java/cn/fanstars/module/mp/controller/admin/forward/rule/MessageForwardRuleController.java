package cn.fanstars.module.mp.controller.admin.forward.rule;

import cn.fanstars.framework.apilog.core.annotation.ApiAccessLog;
import cn.fanstars.framework.common.pojo.CommonResult;
import cn.fanstars.framework.common.pojo.PageParam;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.framework.excel.core.util.ExcelUtils;
import cn.fanstars.module.mp.controller.admin.forward.rule.vo.MessageForwardRulePageReqVO;
import cn.fanstars.module.mp.controller.admin.forward.rule.vo.MessageForwardRuleRespVO;
import cn.fanstars.module.mp.controller.admin.forward.rule.vo.MessageForwardRuleSaveReqVO;
import cn.fanstars.module.mp.dal.dataobject.forward.rule.MessageForwardRuleDO;
import cn.fanstars.module.mp.service.forward.rule.MessageForwardRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.fanstars.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.fanstars.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 转发规则")
@RestController
@RequestMapping("/mp/message-forward-rule")
@Validated
public class MessageForwardRuleController {

    @Resource
    private MessageForwardRuleService messageForwardRuleService;

    @PostMapping("/create")
    @Operation(summary = "创建转发规则")
    @PreAuthorize("@ss.hasPermission('mp:message-forward-rule:create')")
    public CommonResult<Long> createMessageForwardRule(@Valid @RequestBody MessageForwardRuleSaveReqVO createReqVO) {
        return success(messageForwardRuleService.createMessageForwardRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新转发规则")
    @PreAuthorize("@ss.hasPermission('mp:message-forward-rule:update')")
    public CommonResult<Boolean> updateMessageForwardRule(@Valid @RequestBody MessageForwardRuleSaveReqVO updateReqVO) {
        messageForwardRuleService.updateMessageForwardRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除转发规则")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('mp:message-forward-rule:delete')")
    public CommonResult<Boolean> deleteMessageForwardRule(@RequestParam("id") Long id) {
        messageForwardRuleService.deleteMessageForwardRule(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除转发规则")
    @PreAuthorize("@ss.hasPermission('mp:message-forward-rule:delete')")
    public CommonResult<Boolean> deleteMessageForwardRuleList(@RequestParam("ids") List<Long> ids) {
        messageForwardRuleService.deleteMessageForwardRuleListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得转发规则")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mp:message-forward-rule:query')")
    public CommonResult<MessageForwardRuleRespVO> getMessageForwardRule(@RequestParam("id") Long id) {
        MessageForwardRuleDO messageForwardRule = messageForwardRuleService.getMessageForwardRule(id);
        return success(BeanUtils.toBean(messageForwardRule, MessageForwardRuleRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得转发规则分页")
    @PreAuthorize("@ss.hasPermission('mp:message-forward-rule:query')")
    public CommonResult<PageResult<MessageForwardRuleRespVO>> getMessageForwardRulePage(@Valid MessageForwardRulePageReqVO pageReqVO) {
        PageResult<MessageForwardRuleDO> pageResult = messageForwardRuleService.getMessageForwardRulePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MessageForwardRuleRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出转发规则 Excel")
    @PreAuthorize("@ss.hasPermission('mp:message-forward-rule:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMessageForwardRuleExcel(@Valid MessageForwardRulePageReqVO pageReqVO,
                                              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MessageForwardRuleDO> list = messageForwardRuleService.getMessageForwardRulePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "转发规则.xls", "数据", MessageForwardRuleRespVO.class,
                BeanUtils.toBean(list, MessageForwardRuleRespVO.class));
    }

}