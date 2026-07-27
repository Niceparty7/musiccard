package top.yuhanpeng.musiccard.console.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yuhanpeng.musiccard.console.domain.LoginVO;
import top.yuhanpeng.musiccard.module.service.UserService;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author YHP
 * @since 2026-07-26
 */
@RestController
@Slf4j

public class UserController {
    @Resource
    private UserService userService;

    @RequestMapping("/user/login")
    public LoginVO login(@RequestParam(value = "phone") String phone,
                         @RequestParam(value = "password") String password,
                         HttpSession session,
                         HttpServletResponse response) {
        phone = phone == null ? phone : phone.trim();
        password = password == null ? password : password.trim();
        String res = "";
        try {
            res = userService.adminLogin(phone, password, session, response);
        } catch (Exception e) {
            log.error("登录失败", e);
        }
        return new LoginVO().setSign(res);
    }
}