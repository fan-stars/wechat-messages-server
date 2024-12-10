package cn.fan.stars.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.msg-push")
public class MsgPushConfig {

    private List<MsgPushDTO> pushInfos;

    @Getter
    @Setter
    public static class MsgPushDTO {

        /**
         * APPID
         */
        private String appId;
        /**
         * 推送URL
         */
        private String pushUrl;
        /**
         * 优先级, 数值越小, 优先级越高
         */
        private Integer priority;

    }

}
