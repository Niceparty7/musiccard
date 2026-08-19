package com.beat.mall.music.module.auth;

import com.beat.mall.common.utils.SignUtil;
import com.beat.mall.common.response.Response;
import com.beat.mall.music.module.feign.AppUserValidationFeign;
import com.beat.mall.music.module.feign.ConsoleUserValidationFeign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class ProviderAuthService {
    private final AppUserValidationFeign appUserValidationFeign;
    private final ConsoleUserValidationFeign consoleUserValidationFeign;

    public ProviderAuthService(@Lazy AppUserValidationFeign appUserValidationFeign,
                               @Lazy ConsoleUserValidationFeign consoleUserValidationFeign) {
        this.appUserValidationFeign = appUserValidationFeign;
        this.consoleUserValidationFeign = consoleUserValidationFeign;
    }
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
        if (userId == null || !isSuccess(appUserValidationFeign.validate(userId))) {
            throw new SecurityException("登录状态无效");
        }
        return userId;
    }

    public Long requireUser(Long userId) {
        if (userId == null || !isSuccess(consoleUserValidationFeign.validate(userId))) {
            throw new SecurityException("用户未登录");
        }
        return userId;
    }

    public boolean hasValidSign(String sign) {
        Long userId = SignUtil.parseSign(sign);
        return userId != null && isSuccess(appUserValidationFeign.validate(userId));
    }

    private boolean isSuccess(Response<Void> response) {
        return response != null && response.getStatus() != null
                && response.getStatus().getCode() == 1001;
    }
}

