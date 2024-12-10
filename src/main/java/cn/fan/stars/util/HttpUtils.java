package cn.fan.stars.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    public static String get(String url) {
        return get(url, "");
    }

    public static String get(String url, Object paramObj) {
        return get(url, generateUrlParam(paramObj));
    }

    public static String get(String url, String params) {
        return get(url, params, 0);
    }

    public static String get(String url, String params, List<HttpCookie> cookies) {
        return get(url, params, cookies, null, 0);
    }

    public static String get(String url, Object paramObj, List<HttpCookie> cookies) {
        return get(url, generateUrlParam(paramObj), cookies, null, 0);
    }

    public static String get(String url, String params, int timeout) {
        return get(url, params, null, null, timeout);
    }

    public static String get(String url, String params, Map<String, String> headers, int timeout) {
        return get(url, params, null, headers, timeout);
    }

    public static String get(String url, String params, List<HttpCookie> cookies, int timeout) {
        return get(url, params, cookies, null, timeout);
    }

    public static String get(String url, String params, List<HttpCookie> cookies, Map<String, String> headers, int timeout) {
        HttpRequest request = HttpRequest.get(urlAppendParam(url, params));
        if (cookies != null && !cookies.isEmpty()) {
            request.cookie(cookies);
        }
        return execute(request, headers, timeout);
    }

    public static String post(String url) {
        return post(url, null);
    }

    public static String postByUrlParam(String url, Object params) {
//        return post(urlAppendParam(url, generateUrlParam(params)));
        return postByUrlParam(url, params, null);
    }

    public static String postByUrlParam(String url, Object params, Map<String, String> headers) {
        return post(urlAppendParam(url, generateUrlParam(params)), null, headers);
    }

    public static String post(String url, Object params) {
        return post(url, params, 0);
    }

    public static String post(String url, Object params, List<HttpCookie> cookies) {
        return post(url, params, cookies, 0);
    }

    public static String post(String url, Object params, Map<String, String> headers) {
        return post(url, params, null, headers, 0);
    }

    public static String post(String url, Object params, List<HttpCookie> cookies, Map<String, String> headers) {
        return post(url, params, cookies, headers, 0);
    }

    public static String post(String url, Object params, int timeout) {
        return post(url, params, null, null, timeout);
    }

    public static String post(String url, Object params, List<HttpCookie> cookies, int timeout) {
        return post(url, params, cookies, null, timeout);
    }

    public static String post(String url, Object params, List<HttpCookie> cookies, Map<String, String> headers, int timeout) {
        String paramStr = null;
        if (params != null) {
            if (params instanceof String) {
                paramStr = (String) params;
            } else {
                try {
                    paramStr = JSON.toJSONString(params);
                } catch (Exception e) {
                    throw new RuntimeException("Http请求转换 JSON 字符串失败!");
                }
            }
        }
        HttpRequest request = HttpRequest.post(url);
        if (cookies != null && !cookies.isEmpty()) {
            request.cookie(cookies);
        }
        if (paramStr != null) {
            request.body(paramStr);
        }
        return execute(request, headers, timeout);
    }

    public static String upload(String url, File file, Map<String, String> params) {
        HttpRequest request = HttpRequest.post(url);
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                request.form(param.getKey(), param.getValue());
            }
        }
        if (file != null) {
            request.form(file.getName(), file);
        }
        return execute(request, null, 0);
    }

    /**
     * 生成url后面的参数
     */
    private static String generateUrlParam(Object paramObj) {
        JSONObject params = JSONObject.parseObject(JSONObject.toJSONString(paramObj));
        if (params != null) {
            StringBuilder paramSb = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                paramSb.append(param.getKey()).append("=").append(param.getValue()).append("&");
            }
            String paramStr = paramSb.toString();
            if (paramStr.endsWith("&")) {
                paramStr = paramStr.substring(0, paramStr.length() - 1);
            }
            return paramStr;
        }
        return "";
    }

    /**
     * url 后面追加参数
     */
    private static String urlAppendParam(String url, String params) {
        if (params != null && !params.isEmpty()) {
            url = url + (url.contains("?") ? "&" : "?") + params;
        }
        return url;
    }

    /**
     * 执行http请求
     */
    private static String execute(HttpRequest request, Map<String, String> headers, int timeout) {
        try (HttpResponse response = extracted(request, headers, timeout)) {
            return response.body();
        }
    }

    public static HttpResponse extracted(HttpRequest request, Map<String, String> headers, int timeout) {
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getKey() != null && header.getValue() != null) {
                    request.header(header.getKey(), header.getValue());
                }
            }
        }
        if (timeout > 0) {
            request.timeout(timeout);
        }
        return request.execute();
    }

}
