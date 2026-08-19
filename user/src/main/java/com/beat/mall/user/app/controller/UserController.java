package com.beat.mall.user.app.controller;

import com.beat.mall.user.app.feign.UserFeign;
import com.beat.mall.common.api.app.user.UserLoginInfoVo;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.common.utils.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("appUserController")
@RequiredArgsConstructor
public class UserController {
    private final UserFeign userFeign;

    @GetMapping("/user/login/app")
    public Response<UserLoginInfoVo> loginApp(@RequestParam("phone") String phone,
                                              @RequestParam("password") String password,
                                              HttpServletRequest request) {
        return userFeign.login(phone, password, getSign(request), IpUtil.getIpAddress(request));
    }

    @GetMapping("/user/register/app")
    public Response<UserLoginInfoVo> registerApp(@RequestParam("phone") String phone,
                                                 @RequestParam("gender") Integer gender,
                                                 @RequestParam(value = "avatar", required = false) String avatar,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("password") String password,
                                                 @RequestParam(value = "country", required = false) String country,
                                                 @RequestParam(value = "province", required = false) String province,
                                                 @RequestParam(value = "city", required = false) String city,
                                                 HttpServletRequest request) {
        return userFeign.register(phone, gender, avatar, name, password, country,
                province, city, getSign(request), IpUtil.getIpAddress(request));
    }

    private String getSign(HttpServletRequest request) {
        String sign = request.getHeader("sign");
        return BaseUtil.isEmpty(sign) ? request.getParameter("sign") : sign;
    }
}

