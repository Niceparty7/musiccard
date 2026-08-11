package com.beat.mall.app.controller.user;

import com.beat.mall.app.annotations.VerifiedUser;
import com.beat.mall.app.domain.user.UserInfoVo;
import com.beat.mall.app.domain.user.UserLoginInfoVo;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.module.user.service.BaseUserService;
import com.beat.mall.module.user.service.UserDefine;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.IpUtil;
import com.beat.mall.utils.Response;
import com.beat.mall.utils.SignUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private BaseUserService baseUserService;

    @RequestMapping("/user/login/app")
    public Response loginApp(@VerifiedUser User loginUser,
                             @RequestParam(name = "phone") String phone,
                             @RequestParam(name = "password") String password) {
        if (!BaseUtil.isEmpty(loginUser)) {
            return new Response(4004);
        }

        //合法用户直接登录
        boolean result = baseUserService.login(phone, password);
        if (!result) {
            return new Response(4004);
        }
        User user = baseUserService.getByPhone(phone);

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
                .getRequest();
        baseUserService.refreshUserLoginContext(user.getId(), IpUtil.getIpAddress(request), BaseUtil.currentSeconds());

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setGender(user.getGender());
        userInfo.setName(user.getUsername());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserId(user.getId());

        UserLoginInfoVo loginInfo = new UserLoginInfoVo();
        loginInfo.setSign(SignUtil.makeSign(user.getId()));

        loginInfo.setUserInfo(userInfo);
        return new Response(1001, loginInfo);
    }

    @RequestMapping("/user/register/app")
    public Response registerApp(@VerifiedUser User loginUser,
                                @RequestParam(name = "phone") String phone,
                                @RequestParam(name = "gender") Integer gender,
                                @RequestParam(name = "avatar", required = false) String avatar,
                                @RequestParam(name = "name") String name,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "country", required = false) String country,
                                @RequestParam(name = "province", required = false) String province,
                                @RequestParam(name = "city", required = false) String city) {
        if (!BaseUtil.isEmpty(loginUser)) {
            return new Response(4004);
        }

        //考虑用户已经注册了
        //即phone存在
        //直接按照登录处理，返回sign
        User user = baseUserService.extractByPhone(phone, "86");
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes()))
                .getRequest();
        Long newUserId = null;
        if (!BaseUtil.isEmpty(user)) {
            //如果用户被禁止登录
            if (user.getIsDeleted().equals(1) || user.getIsBan().equals(1)) {
                return new Response(1010);
            }
            newUserId = user.getId();
            baseUserService.refreshUserLoginContext(user.getId(), IpUtil.getIpAddress(request),
                    BaseUtil.currentSeconds());
        } else {
            //注册新用户
            if (!UserDefine.isGender(gender)) {
                return new Response(2014);
            }
            boolean registerSuccess = true;
            try {
                newUserId = baseUserService.registerUser(name, phone, gender, avatar, password, country,
                        province, city, IpUtil.getIpAddress(request));
            } catch (Exception exception) {
                registerSuccess = false;
                log.error("注册失败, phone:{}", phone, exception);
            }
            if (!registerSuccess) {
                return new Response(4005);
            }
        }
        user = baseUserService.getById(newUserId);

        UserInfoVo userInfo = new UserInfoVo();
        userInfo.setGender(user.getGender());
        userInfo.setName(user.getUsername());
        userInfo.setPhone(user.getPhone());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setUserId(user.getId());

        UserLoginInfoVo loginInfo = new UserLoginInfoVo();
        loginInfo.setSign(SignUtil.makeSign(user.getId()));

        loginInfo.setUserInfo(userInfo);
        return new Response(1001, loginInfo);
    }
}