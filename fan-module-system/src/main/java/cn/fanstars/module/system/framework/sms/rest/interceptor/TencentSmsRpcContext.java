package cn.fanstars.module.system.framework.sms.rest.interceptor;

/**
 * 腾讯云 RPC 调用上下文：{@code X-TC-Action} 由 RestService 在发起请求前写入，供 {@link TencentSmsAuthInterceptor} 签名使用。
 */
public final class TencentSmsRpcContext {

    private static final ThreadLocal<String> ACTION = new ThreadLocal<>();

    private TencentSmsRpcContext() {
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
