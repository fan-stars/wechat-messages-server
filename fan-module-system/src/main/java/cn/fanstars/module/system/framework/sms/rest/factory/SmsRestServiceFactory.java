package cn.fanstars.module.system.framework.sms.rest.factory;

import cn.fanstars.module.system.framework.sms.core.property.SmsChannelProperties;
import cn.fanstars.module.system.framework.sms.rest.service.AliyunSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.service.QiniuSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.service.SubmailSmsRestService;
import cn.fanstars.module.system.framework.sms.rest.service.TencentSmsRestService;

/**
 * 短信厂商 web-rest 门面工厂
 * <p>
 * 按渠道 ID 缓存 RestService 实例；AK/SK 变更时重建，对标 fan-idc-helper {@code IdcClientApiFactory}。
 */
public interface SmsRestServiceFactory {

    AliyunSmsRestService getAliyunRestService(SmsChannelProperties properties);

    TencentSmsRestService getTencentRestService(SmsChannelProperties properties);

    QiniuSmsRestService getQiniuRestService(SmsChannelProperties properties);

    SubmailSmsRestService getSubmailRestService(SmsChannelProperties properties);

}
