package cn.fanstars.module.system.dal.dataobject.im;

import cn.fanstars.framework.mybatis.core.dataobject.BaseDO;
import cn.fanstars.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * IM 模板与 Webhook 关联 DO
 *
 * @author 繁星源码
 */
@TableName(value = "system_im_template_webhook", autoResultMap = true)
@KeySequence("system_im_template_webhook_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class ImTemplateWebhookDO extends BaseDO {

    @TableId
    private Long id;
    private Long templateId;
    private Long webhookId;

}
