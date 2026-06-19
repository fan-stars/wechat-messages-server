package cn.fanstars.module.system.dal.dataobject.im;

import cn.fanstars.framework.mybatis.core.dataobject.BaseDO;
import cn.fanstars.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * IM 通知发送日志 DO
 *
 * @author 繁星源码
 */
@TableName(value = "system_im_notify_log", autoResultMap = true)
@KeySequence("system_im_notify_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class ImNotifyLogDO extends BaseDO {

    @TableId
    private Long id;
    private Long templateId;
    private String templateCode;
    private Long webhookId;
    private Integer platform;
    private String templateContent;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> templateParams;
    private Integer sendStatus;
    private LocalDateTime sendTime;
    private String apiSendCode;
    private String apiSendMsg;

}
