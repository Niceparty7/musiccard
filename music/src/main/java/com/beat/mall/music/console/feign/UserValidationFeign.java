package com.beat.mall.music.console.feign;

import com.beat.mall.common.response.Response;
import com.beat.mall.music.console.config.ConsoleFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", contextId = "consoleUserValidationFeign", configuration = ConsoleFeignConfiguration.class)
public interface UserValidationFeign {
    @GetMapping("/user/validate")
    Response<Void> validate(@RequestParam("userId") Long userId);
}
