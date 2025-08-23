package cn.fanstars.framework.retrofit2.core.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ApiInfoDTO {

    private String apiHost;
    private String username;
    private String password;

    public ApiInfoDTO(String apiHost) {
        this.apiHost = apiHost;
    }

    public ApiInfoDTO(String apiHost, String username, String password) {
        this.apiHost = apiHost;
        this.username = username;
        this.password = password;
    }

    public String getApiHost() {
        if (StringUtils.hasLength(apiHost)) {
            if (!apiHost.endsWith("/")) {
                return apiHost + "/";
            }
        }
        return apiHost;
    }

}
