package cn.fanstars.module.system.convert.user;

import cn.fanstars.framework.common.util.collection.CollectionUtils;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.system.controller.admin.permission.vo.role.RoleSimpleRespVO;
import cn.fanstars.module.system.controller.admin.user.vo.profile.UserProfileRespVO;
import cn.fanstars.module.system.controller.admin.user.vo.user.UserRespVO;
import cn.fanstars.module.system.controller.admin.user.vo.user.UserSimpleRespVO;
import cn.fanstars.module.system.dal.dataobject.permission.RoleDO;
import cn.fanstars.module.system.dal.dataobject.user.AdminUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    default List<UserRespVO> convertList(List<AdminUserDO> list) {
        return CollectionUtils.convertList(list, user -> convert(user));
    }

    default UserRespVO convert(AdminUserDO user) {
        UserRespVO userVO = BeanUtils.toBean(user, UserRespVO.class);
        return userVO;
    }

    default List<UserSimpleRespVO> convertSimpleList(List<AdminUserDO> list) {
        return CollectionUtils.convertList(list, user -> {
            UserSimpleRespVO userVO = BeanUtils.toBean(user, UserSimpleRespVO.class);
            return userVO;
        });
    }

    default UserProfileRespVO convert(AdminUserDO user, List<RoleDO> userRoles) {
        UserProfileRespVO userVO = BeanUtils.toBean(user, UserProfileRespVO.class);
        userVO.setRoles(BeanUtils.toBean(userRoles, RoleSimpleRespVO.class));
        return userVO;
    }

}
