package cn.fanstars.module.mp.dal.dataobject.forward.log;

import cn.fanstars.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 转发日志 DO
 *
 * @author 繁星源码
 */
@TableName("mp_message_forward_log")
@KeySequence("mp_message_forward_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageForwardLogDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 规则编号
     */
    private Long ruleId;
    /**
     * 消息编号
     */
    private Long messageId;
    /**
     * 公众号
     */
    private Long accountId;
    /**
     * AppId
     */
    private String appId;
    /**
     * OpenID
     */
    private String openid;
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
     * 请求体
     */
    private String requestBody;
    /**
     * 响应体
     */
    private String responseBody;
    /**
     * HTTP状态
     */
    private Integer httpStatus;
    /**
     * 执行状态
     * <p>
     * 枚举 {@link TODO mp_message_forward_log_status 对应的类}
     */
    private Integer status;
    /**
     * 耗时
     */
    private Integer durationMs;
    /**
     * 错误信息
     */
    private String errorMsg;


}