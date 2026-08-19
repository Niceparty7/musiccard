package com.beat.mall.console.controller.user;

import com.alibaba.fastjson.JSON;
import com.beat.mall.common.api.console.user.UserInfoVo;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.common.utils.IpUtil;
import com.beat.mall.common.utils.SpringUtil;
import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.feign.UserFeign;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserFeign userFeign;

    @GetMapping("/user/login/web")
    public Response<UserInfoVo> loginWeb(@VerifiedUser User loginUser,
                                         HttpSession httpSession,
                                         HttpServletRequest request,
                                         @RequestParam("phone") String phone,
                                         @RequestParam("password") String password,
                                         @RequestParam("remember") boolean remember) {
        if (!BaseUtil.isEmpty(loginUser)) {
            return new Response<>(4004);
        }
        Response<UserInfoVo> result = userFeign.login(
                phone, password, remember, IpUtil.getIpAddress(request));
        if (result == null || result.getStatus() == null || result.getStatus().getCode() != 1001
                || result.getResult() == null) {
            return result;
        }
        UserInfoVo info = result.getResult();
        User sessionUser = new User()
                .setId(info.getUserId())
                .setUsername(info.getUserName())
                .setGender(info.getUserGender())
                .setPhone(info.getUserPhone())
                .setAvatar(info.getUserAvatar());
        httpSession.setAttribute(SpringUtil.getProperty("application.session.key"),
                JSON.toJSONString(sessionUser));
        return result;
    }
}
