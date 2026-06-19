package cn.fanstars.module.system.controller.admin.im;

import cn.fanstars.framework.common.pojo.CommonResult;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.system.controller.admin.im.vo.log.ImNotifyLogPageReqVO;
import cn.fanstars.module.system.controller.admin.im.vo.log.ImNotifyLogRespVO;
import cn.fanstars.module.system.dal.dataobject.im.ImNotifyLogDO;
import cn.fanstars.module.system.service.im.ImNotifyLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.fanstars.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - IM 发送日志")
@RestController
@RequestMapping("/system/im-notify-log")
@Validated
public class ImNotifyLogController {

    @Resource
    private ImNotifyLogService imNotifyLogService;

    @GetMapping("/page")
    @Operation(summary = "获得 IM 发送日志分页")
    @PreAuthorize("@ss.hasPermission('system:im-notify-log:query')")
    public CommonResult<PageResult<ImNotifyLogRespVO>> getImNotifyLogPage(@Valid ImNotifyLogPageReqVO pageVO) {
        PageResult<ImNotifyLogDO> pageResult = imNotifyLogService.getImNotifyLogPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ImNotifyLogRespVO.class));
    }

}
