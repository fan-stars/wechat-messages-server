package cn.fanstars.framework.rest.config;

import cn.fanstars.framework.rest.core.HttpServiceFactory;
import cn.fanstars.framework.rest.core.impl.HttpServiceFactoryImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Spring 6 HTTP Interface 自动配置：线程池 + HttpServiceFactory
 */
@AutoConfiguration
@EnableConfigurationProperties(RestClientProperties.class)
public class FanRestAutoConfiguration {

    /**
     * JDK HttpClient 使用的线程池，配置项见 fan.rest.pool.*
     */
    @Bean(destroyMethod = "shutdown") // 容器关闭时释放线程，避免泄漏
    public ThreadPoolTaskExecutor fanRestExecutor(RestClientProperties properties) {
        RestClientProperties.Pool pool = properties.getPool();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(pool.getCoreSize());
        executor.setMaxPoolSize(pool.getMaxSize());
        executor.setQueueCapacity(pool.getQueueCapacity());
        executor.setKeepAliveSeconds(pool.getKeepAliveSeconds());
        executor.setThreadNamePrefix(pool.getThreadNamePrefix());
        executor.initialize(); // 应用上述参数并启动线程池
        return executor;
    }

    /**
     * 声明式 HTTP 客户端工厂，用于创建 RestClient 与 @HttpExchange 接口代理
     */
    @Bean
    public HttpServiceFactory httpServiceFactory(RestClientProperties properties,
                                                 Executor fanRestExecutor,
                                                 List<ClientHttpRequestInterceptor> interceptors) {
        // 收集容器中所有 ClientHttpRequestInterceptor Bean，统一挂到 RestClient 上
        return new HttpServiceFactoryImpl(properties, fanRestExecutor, interceptors);
    }

}
