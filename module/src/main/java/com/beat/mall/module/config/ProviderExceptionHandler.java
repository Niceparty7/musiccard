package com.beat.mall.module.config;

import com.beat.mall.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ProviderExceptionHandler {

    @ExceptionHandler(SecurityException.class)
    public Response<Void> handleSecurityException(SecurityException exception) {
        log.warn("Provider authentication failed: {}", exception.getMessage());
        return new Response<>(1002);
    }

    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception exception) {
        log.error("Provider request failed", exception);
        return new Response<>(4004);
    }
}
