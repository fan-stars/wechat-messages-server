package cn.fanstars.module.system.dal.dataobject.im;

import cn.fanstars.framework.mybatis.core.dataobject.BaseDO;
import cn.fanstars.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * IM Webhook 配置 DO
 *
 * @author 繁星源码
 */
@TableName(value = "system_im_webhook", autoResultMap = true)
@KeySequence("system_im_webhook_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class ImWebhookDO extends BaseDO {

    @TableId
    private Long id;
    private String name;
    private Integer platform;
    private String accessToken;
    private String secret;
    private Integer status;
    private String remark;

}
