package cn.fanstars.module.infra.framework.file.config;

import cn.fanstars.module.infra.framework.file.core.client.FileClientFactory;
import cn.fanstars.module.infra.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 * @author 繁星源码
 */
@Configuration(proxyBeanMethods = false)
public class FanFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}
