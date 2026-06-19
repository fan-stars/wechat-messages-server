package cn.fanstars.module.system.dal.dataobject.im;

import cn.fanstars.framework.mybatis.core.dataobject.BaseDO;
import cn.fanstars.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.util.List;

/**
 * IM 通知模板 DO
 *
 * @author 繁星源码
 */
@TableName(value = "system_im_template", autoResultMap = true)
@KeySequence("system_im_template_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class ImTemplateDO extends BaseDO {

    @TableId
    private Long id;
    private String name;
    private String code;
    private String content;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> params;
    private Integer msgType;
    private Integer status;
    private String remark;

}
