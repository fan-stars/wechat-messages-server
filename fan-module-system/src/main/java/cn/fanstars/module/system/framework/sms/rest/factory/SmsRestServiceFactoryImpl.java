package cn.fanstars.module.system.framework.sms.rest.factory;

import cn.fanstars.framework.rest.core.HttpServiceFactory;
import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.service.AliyunSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.service.QiniuSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.service.SubmailSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.service.TencentSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.service.impl.AliyunSmsRestServiceImpl;
import cn.fanstars.module.system.framework.sms.rest.service.impl.QiniuSmsRestServiceImpl;
import cn.fanstars.module.system.framework.sms.rest.service.impl.SubmailSmsRestServiceImpl;
import cn.fanstars.module.system.framework.sms.rest.service.impl.TencentSmsRestServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信厂商 web-rest 门面工厂实现
 */
@Component
@RequiredArgsConstructor
public class SmsRestServiceFactoryImpl implements SmsRestServiceFactory {

    private final HttpServiceFactory httpServiceFactory;

    private final Map<Long, AliyunSmsRestService> aliyunCache = new ConcurrentHashMap<>();
    private final Map<Long, TencentSmsRestService> tencentCache = new ConcurrentHashMap<>();
    private final Map<Long, QiniuSmsRestService> qiniuCache = new ConcurrentHashMap<>();
    private final Map<Long, SubmailSmsRestService> submailCache = new ConcurrentHashMap<>();

    @Override
    public synchronized AliyunSmsRestService getAliyunRestService(SmsChannelProperties properties) {
        return getOrCreate(aliyunCache, properties, AliyunSmsRestServiceImpl::new);
    }

    @Override
    public synchronized TencentSmsRestService getTencentRestService(SmsChannelProperties properties) {
        return getOrCreate(tencentCache, properties, TencentSmsRestServiceImpl::new);
    }

    @Override
    public synchronized QiniuSmsRestService getQiniuRestService(SmsChannelProperties properties) {
        return getOrCreate(qiniuCache, properties, QiniuSmsRestServiceImpl::new);
    }

    @Override
    public synchronized SubmailSmsRestService getSubmailRestService(SmsChannelProperties properties) {
        return getOrCreate(submailCache, properties, SubmailSmsRestServiceImpl::new);
    }

    private <T> T getOrCreate(Map<Long, T> cache, SmsChannelProperties properties,
                              RestServiceConstructor<T> constructor) {
        Long channelId = properties.getId();
        T cached = cache.get(channelId);
        if (cached != null && sameCredentials(getChannelProperties(cached), properties)) {
            return cached;
        }
        T service = constructor.create(properties, httpServiceFactory);
        cache.put(channelId, service);
        return service;
    }

    private static SmsChannelProperties getChannelProperties(Object service) {
        if (service instanceof AliyunSmsRestService aliyun) {
            return aliyun.getChannelProperties();
        }
        if (service instanceof TencentSmsRestService tencent) {
            return tencent.getChannelProperties();
        }
        if (service instanceof QiniuSmsRestService qiniu) {
            return qiniu.getChannelProperties();
        }
        if (service instanceof SubmailSmsRestService submail) {
            return submail.getChannelProperties();
        }
        throw new IllegalStateException("未知的 RestService 类型: " + service.getClass());
    }

    private static boolean sameCredentials(SmsChannelProperties cached, SmsChannelProperties current) {
        return Objects.equals(cached.getApiKey(), current.getApiKey())
                && Objects.equals(cached.getApiSecret(), current.getApiSecret())
                && Objects.equals(cached.getSignature(), current.getSignature());
    }

    @FunctionalInterface
    private interface RestServiceConstructor<T> {
        T create(SmsChannelProperties properties, HttpServiceFactory httpServiceFactory);
    }

}
