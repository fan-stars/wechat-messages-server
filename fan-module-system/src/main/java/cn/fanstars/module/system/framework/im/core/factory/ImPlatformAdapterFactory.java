package cn.fanstars.module.system.framework.im.core.factory;

import cn.fanstars.module.system.framework.im.core.adapter.ImPlatformAdapter;
import cn.fanstars.module.system.framework.im.core.enums.ImPlatformEnum;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * IM 平台适配器工厂
 *
 * @author 繁星源码
 */
@Component
public class ImPlatformAdapterFactory {

    private final Map<ImPlatformEnum, ImPlatformAdapter> adapterMap;

    public ImPlatformAdapterFactory(List<ImPlatformAdapter> adapters) {
        adapterMap = new EnumMap<>(ImPlatformEnum.class);
        adapters.forEach(adapter -> adapterMap.put(adapter.getPlatform(), adapter));
    }

    /**
     * 按平台获取适配器
     *
     * @param platform 平台枚举值
     * @return 适配器
     */
    public ImPlatformAdapter getAdapter(Integer platform) {
        ImPlatformEnum platformEnum = ImPlatformEnum.valueOfPlatform(platform);
        if (platformEnum == null) {
            return null;
        }
        return adapterMap.get(platformEnum);
    }

}
