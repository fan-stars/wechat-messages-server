package cn.fanstars.module.mp.controller.admin.forward.log;

import cn.fanstars.framework.apilog.core.annotation.ApiAccessLog;
import cn.fanstars.framework.common.pojo.CommonResult;
import cn.fanstars.framework.common.pojo.PageParam;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.framework.excel.core.util.ExcelUtils;
import cn.fanstars.module.mp.controller.admin.forward.log.vo.MessageForwardLogPageReqVO;
import cn.fanstars.module.mp.controller.admin.forward.log.vo.MessageForwardLogRespVO;
import cn.fanstars.module.mp.dal.dataobject.forward.log.MessageForwardLogDO;
import cn.fanstars.module.mp.service.forward.log.MessageForwardLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import static cn.fanstars.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.fanstars.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 转发日志")
@RestController
@RequestMapping("/mp/message-forward-log")
@Validated
public class MessageForwardLogController {

    @Resource
    private MessageForwardLogService messageForwardLogService;

    @GetMapping("/get")
    @Operation(summary = "获得转发日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('mp:message-forward-log:query')")
    public CommonResult<MessageForwardLogRespVO> getMessageForwardLog(@RequestParam("id") Long id) {
        MessageForwardLogDO messageForwardLog = messageForwardLogService.getMessageForwardLog(id);
        return success(BeanUtils.toBean(messageForwardLog, MessageForwardLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得转发日志分页")
    @PreAuthorize("@ss.hasPermission('mp:message-forward-log:query')")
    public CommonResult<PageResult<MessageForwardLogRespVO>> getMessageForwardLogPage(@Valid MessageForwardLogPageReqVO pageReqVO) {
        PageResult<MessageForwardLogDO> pageResult = messageForwardLogService.getMessageForwardLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MessageForwardLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出转发日志 Excel")
    @PreAuthorize("@ss.hasPermission('mp:message-forward-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMessageForwardLogExcel(@Valid MessageForwardLogPageReqVO pageReqVO,
                                             HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MessageForwardLogDO> list = messageForwardLogService.getMessageForwardLogPage(pageReqVO).getList();
        ExcelUtils.write(response, "转发日志.xls", "数据", MessageForwardLogRespVO.class,
                BeanUtils.toBean(list, MessageForwardLogRespVO.class));
    }

}
