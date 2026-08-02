package com.beat.mall.console.controller.user;

import com.alibaba.fastjson.JSON;
import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.domain.user.UserInfoVo;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.module.user.service.BaseUserService;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.IpUtil;
import com.beat.mall.utils.Response;
import com.beat.mall.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@Slf4j
public class UserController {
    @Autowired
    BaseUserService baseUserService;

    @RequestMapping("/user/login/web")
    public Response loginWeb(@VerifiedUser User loginUser,
                             HttpSession httpSession,
                             @RequestParam(name = "phone") String phone,
                             @RequestParam(name = "password") String password,
                             @RequestParam(name = "remember") boolean remember) {
        if (!BaseUtil.isEmpty(loginUser)) {
            return new Response(4004);
        }

        boolean result;
        if (remember) {
            result = baseUserService.login(phone, password);
        } else {
            result = baseUserService.login(phone, "86", password, false,
                    false, 0);
        }
        if (!result) {
            return new Response(1010);
        }

        User user = baseUserService.getByPhone(phone);
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
                .getRequest();
        baseUserService.refreshUserLoginContext(user.getId(), IpUtil.getIpAddress(request), BaseUtil.currentSeconds());

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setUserGender(user.getGender());
        userInfo.setUserName(user.getUsername());
        userInfo.setUserPhone(user.getPhone());
        userInfo.setUserAvatar(user.getAvatar());

        // 写session
        httpSession.setAttribute(SpringUtil.getProperty("application.session.key"), JSON.toJSONString(user));

        return new Response(1001, userInfo);
    }

}
