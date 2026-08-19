package com.beat.mall.user.config;

import com.beat.mall.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response<Void> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return new Response<>(4004);
    }
}
