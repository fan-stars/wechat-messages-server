package cn.fanstars.module.mp.framework.web.config;

import cn.fanstars.framework.swagger.config.FanSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mp 模块的 web 组件的 Configuration
 *
 * @author 繁星源码
 */
@Configuration(proxyBeanMethods = false)
public class MpWebConfiguration {

    /**
     * mp 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi mpGroupedOpenApi() {
        return FanSwaggerAutoConfiguration.buildGroupedOpenApi("mp");
    }

}
