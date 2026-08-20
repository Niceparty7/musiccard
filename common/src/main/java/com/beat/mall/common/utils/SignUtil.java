package com.beat.mall.common.utils;

import com.alibaba.fastjson.JSON;
import com.beat.mall.common.entity.user.UserSign;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class SignUtil {
    private static final String SIGN_SALT = "_2019_dream";
    private static final int EXPIRATION_TIME = 1209600;
    private static final String PASSWORD_SALT = "__MLZ_RED";

    private SignUtil() {
    }

    public static int getExpirationTime() {
        return EXPIRATION_TIME;
    }

    public static String makeSign(Long userId) {
        UserSign sign = new UserSign()
                .setExpiration(BaseUtil.currentSeconds() + EXPIRATION_TIME)
                .setSalt(SIGN_SALT)
                .setUserId(userId);
        byte[] rawSign = Base64.getEncoder()
                .encode(JSON.toJSONString(sign).getBytes(StandardCharsets.UTF_8));
        return new String(rawSign, StandardCharsets.UTF_8).trim();
    }

    public static Long parseSign(String sign) {
        if (BaseUtil.isEmpty(sign)) {
            return null;
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(sign.getBytes(StandardCharsets.UTF_8));
            UserSign userSign = JSON.parseObject(
                    new String(bytes, StandardCharsets.UTF_8), UserSign.class);
            if (userSign == null || BaseUtil.currentSeconds() > userSign.getExpiration()) {
                return null;
            }
            return userSign.getUserId();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static String marshal(String password) {
        return BaseUtil.md5(PASSWORD_SALT + password);
    }
}
