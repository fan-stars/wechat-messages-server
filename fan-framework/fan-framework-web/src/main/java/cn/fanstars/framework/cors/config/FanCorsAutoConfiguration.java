package cn.fanstars.framework.cors.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@AutoConfiguration
@EnableConfigurationProperties(CorsProperties.class)
@ConditionalOnProperty(prefix = "fan.cors", name = "enable", havingValue = "true", matchIfMissing = true) // 设置为 false 时，禁用
public class FanCorsAutoConfiguration {

    /**
     * 创建 CorsFilter Bean，解决跨域问题
     */
    @Bean
    public CorsFilter corsFilter(CorsProperties properties) {
        // 创建 CorsConfiguration 对象
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern(properties.getAllowedOriginPattern()); // 设置访问源地址
        config.addAllowedHeader(properties.getAllowedHeader()); // 设置访问源请求头
        config.addAllowedMethod(properties.getAllowedMethod()); // 设置访问源请求方法
        config.setAllowCredentials(properties.getAllowCredentials());  // 是否允许携带凭证（如 cookies）
        config.setMaxAge(properties.getMaxAge());  // 预检请求的缓存时间（秒）

        if (!properties.getAllowedOrigins().isEmpty()) {
            config.setAllowedOrigins(properties.getAllowedOrigins());  // 允许的域名
        }
        if (!properties.getAllowedMethods().isEmpty()) {
            config.setAllowedMethods(properties.getAllowedMethods());  // 允许的请求方法
        }
        if (!properties.getAllowedHeaders().isEmpty()) {
            config.setAllowedHeaders(properties.getAllowedHeaders());  // 允许的请求头
        }
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // 对所有接口都有效
        return new CorsFilter(source);
    }

}
