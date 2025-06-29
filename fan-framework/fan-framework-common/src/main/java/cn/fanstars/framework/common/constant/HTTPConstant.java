package cn.fanstars.framework.common.constant;

public interface HTTPConstant {

    String HEADER_AUTHORIZATION = "Authorization";

    String HEADER_AUTHORIZATION_PREFIX = "Bearer ";

    String HEADER_OPENAI_AUTHORIZATION = HEADER_AUTHORIZATION;

    String HEADER_OPENAI_AUTHORIZATION_PREFIX = HEADER_AUTHORIZATION_PREFIX;

    int JSON_RESULT_SUCCESS_CODE = 0;

    String JSON_RESULT_SUCCESS_MSG = "success";

    int JSON_RESULT_ERROR_CODE = -1;

    String JSON_RESULT_ERROR_MSG = "error";

    String BE_IN_COMMON_USE_MIME_TYPE = "application/octet-stream";

    String JSON_CONTENT_TYPE = "application/json; charset=utf-8;";

    String FORM_CONTENT_TYPE = "multipart/form-data";

    String HEAD_USER_AGENT_KEY = "-UserAgent";

    String CONTENT_DISPOSITION_KEY = "Content-Disposition";

    String CONTENT_DISPOSITION_VALUE_PREFIX = "attachment;filename=";

    String TOKEN_KEY_NAME = "token";

    String USER_AGENT_NAME = "USER-AGENT";

    String MSIE_BROWSER_NAME = "MSIE";

    String FIREFOX_BROWSER_NAME = "Firefox";

    String CHROME_BROWSER_NAME = "Chrome";

    int HTTP_RESPONSE_STATUS = 200;

    int HTTP_UNAUTHORIZED_STATUS = 401;
}
