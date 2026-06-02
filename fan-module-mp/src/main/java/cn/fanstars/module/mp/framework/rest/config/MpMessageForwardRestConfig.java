package cn.fanstars.module.mp.framework.rest.config;

import cn.fanstars.module.mp.framework.rest.service.MpMessageForwardClient;
import cn.fanstars.module.mp.framework.rest.service.impl.MpMessageForwardClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * 公众号消息转发 RestClient 配置
 * <p>
 * 注册 {@link MpMessageForwardClient}，复用框架 {@code fanRestExecutor} 与全局 {@link ClientHttpRequestInterceptor}。
 */
@Configuration
public class MpMessageForwardRestConfig {

    @Bean
    public MpMessageForwardClient mpMessageForwardClient(Executor fanRestExecutor,
                                                         List<ClientHttpRequestInterceptor> interceptors) {
        return new MpMessageForwardClientImpl(fanRestExecutor, interceptors);
    }

}
