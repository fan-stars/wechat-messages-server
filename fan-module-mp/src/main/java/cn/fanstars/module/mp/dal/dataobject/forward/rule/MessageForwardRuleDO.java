package cn.fanstars.module.mp.dal.dataobject.forward.rule;

import cn.fanstars.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 转发规则 DO
 *
 * @author 繁星源码
 */
@TableName("mp_message_forward_rule")
@KeySequence("mp_message_forward_rule_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageForwardRuleDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 公众号
     */
    private Long accountId;
    /**
     * 规则名称
     */
    private String name;
    /**
     * 状态
     * <p>
     * 枚举 {@link TODO common_status 对应的类}
     */
    private Integer status;
    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 转发模式
     * <p>
     * 枚举 {@link TODO mp_message_forward_mode 对应的类}
     */
    private Integer forwardMode;
    /**
     * 接收响应
     * <p>
     * 枚举 {@link TODO infra_boolean_string 对应的类}
     */
    private Boolean receiveResponse;
    /**
     * 响应回复
     * <p>
     * 枚举 {@link TODO infra_boolean_string 对应的类}
     */
    private Boolean useResponseAsReply;
    /**
     * 目标地址
     */
    private String targetUrl;
    /**
     * 超时
     */
    private Integer timeoutMs;
    /**
     * 消息类型
     */
    private String messageTypes;
    /**
     * 记录日志
     * <p>
     * 枚举 {@link TODO infra_boolean_string 对应的类}
     */
    private Boolean enableLog;
    /**
     * 备注
     */
    private String remark;


}