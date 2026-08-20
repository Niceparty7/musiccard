package com.beat.mall.common.utils;

import java.security.SecureRandom;
import java.util.Base64;

public final class SaltUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private SaltUtil() {
    }

    public static String generateSalt(int length) {
        byte[] salt = new byte[length];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
