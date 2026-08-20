package com.beat.mall.user.module.api.console.controller;

import com.beat.mall.common.api.console.user.UserInfoVo;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.user.module.user.service.BaseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("consoleUserProviderController")
@RequiredArgsConstructor
@RequestMapping(headers = "X-Internal-Token")
public class UserProviderController {
    private final BaseUserService baseUserService;

    @GetMapping(value = "/user/validate", headers = "X-Client-Type=console")
    public Response<Void> validate(@RequestParam("userId") Long userId) {
        return baseUserService.getById(userId) == null ? new Response<>(1002) : new Response<>(1001);
    }

    @RequestMapping("/user/login/web")
    public Response<UserInfoVo> loginWeb(
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam boolean remember,
            @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp) {
        boolean loginSuccess = remember
                ? baseUserService.login(phone, password)
                : baseUserService.login(phone, "86", password, false, false, 0);
        if (!loginSuccess) {
            return new Response<>(1010);
        }
        User user = baseUserService.getByPhone(phone);
        baseUserService.refreshUserLoginContext(
                user.getId(), clientIp, BaseUtil.currentSeconds());
        UserInfoVo result = new UserInfoVo()
                .setUserId(user.getId())
                .setUserName(user.getUsername())
                .setUserGender(user.getGender())
                .setUserPhone(user.getPhone())
                .setUserAvatar(user.getAvatar());
        return new Response<>(1001, result);
    }
}



