package com.beat.mall.console.feign;

import com.beat.mall.common.api.console.user.UserInfoVo;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "module", contextId = "consoleUserFeign")
public interface ConsoleUserFeign {
    @GetMapping("/user/login/web")
    Response<UserInfoVo> login(@RequestParam("phone") String phone,
                               @RequestParam("password") String password,
                               @RequestParam("remember") boolean remember,
                               @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp);
}
