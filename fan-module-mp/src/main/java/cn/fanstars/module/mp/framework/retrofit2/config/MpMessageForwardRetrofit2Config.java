package cn.fanstars.module.mp.framework.retrofit2.config;

import cn.fanstars.framework.okhttp.core.OkHttpFactory;
import cn.fanstars.module.mp.framework.retrofit2.service.MpMessageForwardClient;
import cn.fanstars.module.mp.framework.retrofit2.service.impl.MpMessageForwardClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公众号消息转发 Retrofit2 配置
 * <p>
 * 注册 {@link MpMessageForwardClient}，复用框架 {@link OkHttpFactory} 连接池。
 */
@Configuration
public class MpMessageForwardRetrofit2Config {

    @Bean
    public MpMessageForwardClient mpMessageForwardClient(OkHttpFactory okHttpFactory) {
        return new MpMessageForwardClientImpl(okHttpFactory);
    }

}
