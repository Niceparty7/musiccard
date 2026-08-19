package com.beat.mall.module.api.console.controller;

import com.beat.mall.common.api.console.user.UserInfoVo;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.module.user.service.BaseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(headers = "X-Client-Type=console")
public class ConsoleUserProviderController {
    private final BaseUserService baseUserService;

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
