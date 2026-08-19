package com.beat.mall.music.app.feign;

import com.beat.mall.common.response.Response;
import com.beat.mall.music.app.config.AppFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", contextId = "appUserValidationFeign", configuration = AppFeignConfiguration.class)
public interface UserValidationFeign {
    @GetMapping("/user/validate")
    Response<Void> validate(@RequestParam("userId") Long userId);
}
