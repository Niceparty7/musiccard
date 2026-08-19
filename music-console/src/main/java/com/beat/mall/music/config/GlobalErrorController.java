package com.beat.mall.music.config;

import com.beat.mall.common.response.Response;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalErrorController implements ErrorController {
    @RequestMapping("/error")
    public Response<Void> error() {
        return new Response<>(4004);
    }
}
