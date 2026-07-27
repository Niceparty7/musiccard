package top.yuhanpeng.musiccard.app.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yuhanpeng.musiccard.app.domain.LoginVO;
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
                         @RequestParam(value = "password") String password) {
        phone = phone == null ? phone : phone.trim();
        password = password == null ? password : password.trim();
        String res = "";
        try {
            res = userService.login(phone, password);
        } catch (Exception e) {
            res = "登录失败";
            log.error("登录失败", e);
        }
        return new LoginVO().setSign(res);
    }

    @RequestMapping("/user/register")
    public LoginVO register(@RequestParam(value = "phone") String phone,
                            @RequestParam(value = "password") String password,
                            @RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "avatar", required = false) String avatar) {
        phone = phone == null ? phone : phone.trim();
        password = password == null ? password : password.trim();
        name = name == null ? name : name.trim();
        String res = "";
        try {
            res = userService.register(phone, password, name, avatar);
        } catch (Exception e) {
            res = "注册失败";
            log.error("注册失败", e);
        }
        return new LoginVO().setSign(res);
    }
}