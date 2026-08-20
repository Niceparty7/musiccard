package com.beat.mall.user.console.feign;

import com.beat.mall.user.console.config.ConsoleFeignConfiguration;

import com.beat.mall.common.api.console.user.UserInfoVo;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", contextId = "consoleUserFeign", configuration = ConsoleFeignConfiguration.class)
public interface UserFeign {
    @GetMapping("/user/login/web")
    Response<UserInfoVo> login(@RequestParam("phone") String phone,
                               @RequestParam("password") String password,
                               @RequestParam("remember") boolean remember,
                               @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp);
}


