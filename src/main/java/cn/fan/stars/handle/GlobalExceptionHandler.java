package cn.fan.stars.handle;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @author zengfj
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WxErrorException.class)
    public String handleWxErrorException(WxErrorException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("request url: {}, WxErrorException error. ", requestURI, e);
        return "";
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("request url: {}, Exception error. ", requestURI, e);
        return "fail";
    }

}
