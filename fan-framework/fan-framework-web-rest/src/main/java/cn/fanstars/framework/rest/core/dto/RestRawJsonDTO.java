package cn.fanstars.framework.rest.core.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * RestClient 响应基类：由 {@link cn.fanstars.framework.rest.core.converters.FastJsonRestMessageConverters}
 * 在启用原始响应捕获时注入 {@link #rawJson}，供下游验签等场景使用。
 *
 * @author aquan
 */
@Getter
@Setter
public abstract class RestRawJsonDTO {

    /**
     * HTTP 响应原始 JSON，由 MessageConverter 注入，不参与 JSON 序列化/反序列化
     */
    @ToString.Exclude
    @JSONField(serialize = false, deserialize = false)
    private String rawJson;

}
