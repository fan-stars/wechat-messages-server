package cn.fanstars.module.system.framework.sms.rest.interceptor;

/**
 * 阿里云 RPC 调用上下文：{@code x-acs-action} 由 RestService 在发起请求前写入，供 {@link AliyunSmsAuthInterceptor} 签名使用。
 */
public final class AliyunSmsRpcContext {

    private static final ThreadLocal<String> ACTION = new ThreadLocal<>();

    private AliyunSmsRpcContext() {
    }

    public static void setAction(String action) {
        ACTION.set(action);
    }

    public static String getAction() {
        return ACTION.get();
    }

    public static void clear() {
        ACTION.remove();
    }

}
