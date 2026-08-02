package com.beat.mall.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class SaltUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateSalt(int length) {
        byte[] salt = new byte[length];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}