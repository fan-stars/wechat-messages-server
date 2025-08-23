package cn.fanstars.framework.okhttp.config;

import cn.fanstars.framework.okhttp.core.OkHttpFactory;
import cn.fanstars.framework.okhttp.core.impl.OkHttpFactoryImpl;
import okhttp3.Interceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties({OkHttpProperties.class})
public class FanOkHttpAutoConfiguration {

    @Bean
    public OkHttpFactory okHttpFactory(OkHttpProperties okHttpProperties, List<Interceptor> interceptors) {
        return new OkHttpFactoryImpl(okHttpProperties, interceptors);
    }

}
