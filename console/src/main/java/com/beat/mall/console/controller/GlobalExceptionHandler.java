package com.beat.mall.console.controller;

import com.beat.mall.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response handleException(Exception e) {
        log.error(e.getMessage(), e);
        return new Response(4004, e.getStackTrace());
    }
}
