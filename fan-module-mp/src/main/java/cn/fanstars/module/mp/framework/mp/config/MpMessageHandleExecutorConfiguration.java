package cn.fanstars.module.mp.framework.mp.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 微信公众号消息回调编排线程池
 * <p>
 * TaskDecorator 由 {@link cn.fanstars.framework.quartz.config.FanAsyncAutoConfiguration} 统一注入 TtlRunnable。
 */
@Configuration
@EnableConfigurationProperties(MpMessageHandleProperties.class)
public class MpMessageHandleExecutorConfiguration {

    public static final String MP_MESSAGE_HANDLE_EXECUTOR = "mpMessageHandleExecutor";

    /**
     * 消息回调编排专用线程池（入库、同步转发 HTTP、本地 Router 并行任务）
     */
    @Bean(name = MP_MESSAGE_HANDLE_EXECUTOR)
    public ThreadPoolTaskExecutor mpMessageHandleExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 常驻线程，承担入库 / 转发 / Router
        executor.setCorePoolSize(8);
        // 峰值扩容
        executor.setMaxPoolSize(32);
        executor.setKeepAliveSeconds(60);
        // 排队任务上限
        executor.setQueueCapacity(200);
        // 日志中便于识别
        executor.setThreadNamePrefix("mp-message-handle-");
        // 队列满时由调用线程执行，避免微信回调线程直接丢弃任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
