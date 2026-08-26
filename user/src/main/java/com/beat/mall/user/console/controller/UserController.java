package com.beat.mall.user.console.controller.user;

import com.alibaba.fastjson.JSON;
import com.beat.mall.common.api.console.user.UserInfoVo;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.common.utils.IpUtil;
import com.beat.mall.common.utils.SpringUtil;
import com.beat.mall.user.console.annotations.VerifiedUser;
import com.beat.mall.user.console.feign.UserFeign;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("consoleUserController")
@RequiredArgsConstructor
public class UserController {
    private final UserFeign userFeign;

    @GetMapping("/user/login/web")
    public Response<UserInfoVo> loginWeb(@VerifiedUser User loginUser,
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
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute(SpringUtil.getProperty("application.session.key"),
                JSON.toJSONString(sessionUser));
        return result;
    }

    @PostMapping("/user/logout/web")
    public Response<String> logoutWeb(HttpServletRequest request,
                                      HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        String cookieName = SpringUtil.getProperty("server.servlet.session.cookie.name");
        if (BaseUtil.isEmpty(cookieName)) {
            cookieName = "SESSION";
        }
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
        return new Response<>(1001, "退出成功");
    }
}


