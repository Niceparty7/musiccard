package com.beat.mall.user.app.feign;

import com.beat.mall.user.app.config.AppFeignConfiguration;

import com.beat.mall.common.api.app.user.UserLoginInfoVo;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", contextId = "appUserFeign", configuration = AppFeignConfiguration.class)
public interface UserFeign {
    @GetMapping("/user/login/app")
    Response<UserLoginInfoVo> login(
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestHeader(value = "sign", required = false) String sign,
            @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp);

    @GetMapping("/user/register/app")
    Response<UserLoginInfoVo> register(
            @RequestParam("phone") String phone,
            @RequestParam("gender") Integer gender,
            @RequestParam(value = "avatar", required = false) String avatar,
            @RequestParam("name") String name,
            @RequestParam("password") String password,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "province", required = false) String province,
            @RequestParam(value = "city", required = false) String city,
            @RequestHeader(value = "sign", required = false) String sign,
            @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp);
}


