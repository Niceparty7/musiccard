package com.beat.mall.music.module.feign;

import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", contextId = "moduleConsoleUserValidationFeign", configuration = ConsoleUserValidationFeignConfiguration.class)
public interface ConsoleUserValidationFeign {
    @GetMapping("/user/validate")
    Response<Void> validate(@RequestParam("userId") Long userId);
}
