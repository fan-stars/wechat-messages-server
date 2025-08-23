package cn.fanstars.framework.retrofit2.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class FanRetrofit2AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RetrofitCache retrofitCache() {
        return new DefaultRetrofitCache();
    }

}
