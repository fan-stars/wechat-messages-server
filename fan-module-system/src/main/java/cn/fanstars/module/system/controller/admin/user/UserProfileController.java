package cn.fanstars.module.system.controller.admin.user;

import cn.fanstars.framework.common.pojo.CommonResult;
import cn.fanstars.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import cn.fanstars.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import cn.fanstars.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import cn.fanstars.module.system.convert.user.UserConvert;
import cn.fanstars.module.system.dal.dataobject.permission.RoleDO;
import cn.fanstars.module.system.dal.dataobject.user.AdminUserDO;
import cn.fanstars.module.system.service.permission.PermissionService;
import cn.fanstars.module.system.service.permission.RoleService;
import cn.fanstars.module.system.service.user.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.fanstars.framework.common.pojo.CommonResult.success;
import static cn.fanstars.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 用户个人中心")
@RestController
@RequestMapping("/system/user/profile")
@Validated
@Slf4j
public class UserProfileController {

    @Resource
    private AdminUserService userService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private RoleService roleService;

    @GetMapping("/get")
    @Operation(summary = "获得登录用户信息")
    public CommonResult<UserProfileRespVO> getUserProfile() {
        // 获得用户基本信息
        AdminUserDO user = userService.getUser(getLoginUserId());
        // 获得用户角色
        List<RoleDO> userRoles = roleService.getRoleListFromCache(permissionService.getUserRoleIdListByUserId(user.getId()));
        return success(UserConvert.INSTANCE.convert(user, userRoles));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户个人信息")
    public CommonResult<Boolean> updateUserProfile(@Valid @RequestBody UserProfileUpdateReqVO reqVO) {
        userService.updateUserProfile(getLoginUserId(), reqVO);
        return success(true);
    }

    @PutMapping("/update-password")
    @Operation(summary = "修改用户个人密码")
    public CommonResult<Boolean> updateUserProfilePassword(@Valid @RequestBody UserProfileUpdatePasswordReqVO reqVO) {
        userService.updateUserPassword(getLoginUserId(), reqVO);
        return success(true);
    }

}
