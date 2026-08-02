package com.beat.mall.utils;

import com.alibaba.fastjson.JSON;
import com.beat.mall.module.user.entity.UserSign;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class SignUtil {

    private final static String SIGN_SALT = "_2019_dream";
    private final static int EXPIRATION_TIME = 1209600;
    private static final String PASSWORD_SALT = "__MLZ_RED";

    private SignUtil() {

    }

    public static int getExpirationTime() {
        return EXPIRATION_TIME;
    }

    public static String makeSign(Long userId) {
        UserSign sign = new UserSign();
        sign.setExpiration(BaseUtil.currentSeconds() + EXPIRATION_TIME);
        sign.setSalt(SIGN_SALT);
        sign.setUserId(userId);
        byte[] rawSign = Base64.getEncoder().encode(JSON.toJSONString(sign).getBytes(StandardCharsets.UTF_8));
        return new String(rawSign, StandardCharsets.UTF_8).trim();
    }

    public static Long parseSign(String sign) {
        if (BaseUtil.isEmpty(sign)) {
            return null;
        }

        byte[] bytes = Base64.getDecoder().decode(sign.getBytes(StandardCharsets.UTF_8));
        String rawSign = new String(bytes, StandardCharsets.UTF_8);
        UserSign userSign = null;
        try {
            userSign = JSON.parseObject(rawSign, UserSign.class);
        } catch (Exception cause) {
            // ignores
        }
        if (userSign == null) {
            return null;
        }

        int time = userSign.getExpiration();
        int current = BaseUtil.currentSeconds();
        if (current > time) {
            // a expired sign
            return null;
        }

        return userSign.getUserId();
    }

    public static String marshal(String password) {
        return BaseUtil.md5(PASSWORD_SALT + password);
    }

}
