package cn.fanstars.framework.common.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MenuVO {

    private String id;
    private Long menuId;
    private Long menuParentId;
    private String path;
    private String icon;
    private String title;
    private List<MenuVO> children;

}
