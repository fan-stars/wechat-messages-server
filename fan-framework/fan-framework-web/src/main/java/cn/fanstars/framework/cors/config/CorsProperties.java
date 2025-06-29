package cn.fanstars.framework.cors.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

@Data
@ConfigurationProperties("fan.cors")
public class CorsProperties {

    private Boolean enable = false;

    private String allowedOriginPattern = "*";

    private String allowedHeader = "*";

    private String allowedMethod = "*";

    private List<String> allowedOrigins = Collections.emptyList();

    private List<String> allowedMethods = Collections.emptyList();

    private List<String> allowedHeaders = Collections.emptyList();

    private Boolean allowCredentials = true;

    private Long maxAge = 3600L;

}