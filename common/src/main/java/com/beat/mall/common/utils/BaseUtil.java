package com.beat.mall.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public final class BaseUtil {
    private BaseUtil() {
    }

    public static String md5(String text) {
        try {
            return DigestUtils.md5Hex(text);
        } catch (Exception e) {
            log.error("Md5 error", e);
            return "";
        }
    }

    public static int currentSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static boolean isEmpty(Object obj) {
        if (obj instanceof List) {
            return ((List<?>) obj).isEmpty();
        }
        if (obj instanceof Number) {
            try {
                return new DecimalFormat().parse(obj.toString()).doubleValue() == 0;
            } catch (ParseException e) {
                log.error("Number parse error", e);
                return false;
            }
        }
        return obj == null || "".equals(obj.toString());
    }

    public static boolean isNumeric(String str) {
        return str != null && Pattern.matches("[0-9]*", str);
    }
}
