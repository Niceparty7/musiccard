package com.beat.mall.user.module.auth;

import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.utils.SignUtil;
import com.beat.mall.user.module.user.service.BaseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final BaseUserService userService;

    @Value("${microservice.internal-token}")
    private String internalToken;

    public void checkInternalToken(String token) {
        if (token == null || internalToken == null
                || !MessageDigest.isEqual(
                internalToken.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8))) {
            throw new SecurityException("非法服务调用");
        }
    }

    public Long requireSign(String sign) {
        Long userId = SignUtil.parseSign(sign);
        if (userId == null || userService.getById(userId) == null) {
            throw new SecurityException("登录状态无效");
        }
        return userId;
    }

    public User requireUser(Long userId) {
        if (userId == null) {
            throw new SecurityException("用户未登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            throw new SecurityException("用户不存在");
        }
        return user;
    }

    public boolean hasValidSign(String sign) {
        Long userId = SignUtil.parseSign(sign);
        return userId != null && userService.getById(userId) != null;
    }
}

