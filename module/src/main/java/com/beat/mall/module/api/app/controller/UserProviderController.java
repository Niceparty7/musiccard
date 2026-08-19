package com.beat.mall.module.api.app.controller;

import com.beat.mall.common.api.app.user.UserInfoVo;
import com.beat.mall.common.api.app.user.UserLoginInfoVo;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.common.utils.SignUtil;
import com.beat.mall.module.auth.ProviderAuthService;
import com.beat.mall.module.user.service.BaseUserService;
import com.beat.mall.module.user.service.UserDefine;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("appUserProviderController")
@RequiredArgsConstructor
@RequestMapping(headers = "X-Client-Type=app")
public class UserProviderController {
    private final BaseUserService baseUserService;
    private final ProviderAuthService providerAuthService;

    @RequestMapping("/user/login/app")
    public Response<UserLoginInfoVo> loginApp(
            @RequestParam String phone,
            @RequestParam String password,
            @RequestHeader(value = "sign", required = false) String sign,
            @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp) {
        if (providerAuthService.hasValidSign(sign)) {
            return new Response<>(4004);
        }
        if (!baseUserService.login(phone, password)) {
            return new Response<>(4004);
        }
        User user = baseUserService.getByPhone(phone);
        baseUserService.refreshUserLoginContext(
                user.getId(), clientIp, BaseUtil.currentSeconds());
        return new Response<>(1001, buildLoginInfo(user));
    }

    @RequestMapping("/user/register/app")
    public Response<UserLoginInfoVo> registerApp(
            @RequestParam String phone,
            @RequestParam Integer gender,
            @RequestParam(required = false) String avatar,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String city,
            @RequestHeader(value = "sign", required = false) String sign,
            @RequestHeader(value = "X-Forwarded-For", required = false) String clientIp) {
        if (providerAuthService.hasValidSign(sign)) {
            return new Response<>(4004);
        }
        User user = baseUserService.extractByPhone(phone, "86");
        Long userId;
        if (user != null) {
            if (Integer.valueOf(1).equals(user.getIsDeleted())
                    || Integer.valueOf(1).equals(user.getIsBan())) {
                return new Response<>(1010);
            }
            userId = user.getId();
            baseUserService.refreshUserLoginContext(userId, clientIp, BaseUtil.currentSeconds());
        } else {
            if (!UserDefine.isGender(gender)) {
                return new Response<>(2014);
            }
            userId = baseUserService.registerUser(
                    name, phone, gender, avatar, password,
                    country, province, city, clientIp);
        }
        return new Response<>(1001, buildLoginInfo(baseUserService.getById(userId)));
    }

    private UserLoginInfoVo buildLoginInfo(User user) {
        UserInfoVo userInfo = new UserInfoVo()
                .setUserId(user.getId())
                .setName(user.getUsername())
                .setGender(user.getGender())
                .setPhone(user.getPhone())
                .setAvatar(user.getAvatar());
        return new UserLoginInfoVo()
                .setUserInfo(userInfo)
                .setSign(SignUtil.makeSign(user.getId()));
    }
}
