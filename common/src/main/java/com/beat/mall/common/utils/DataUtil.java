package com.beat.mall.common.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DataUtil {
    private static final String[] OFFICIAL_PHONE = {"***"};

    private DataUtil() {
    }

    public static boolean isPhoneNumber(String phone) {
        if (ArrayUtils.contains(OFFICIAL_PHONE, phone)) {
            return true;
        }
        return phone != null && Pattern.matches("[1|2]\\d{10}", phone);
    }

    public static boolean isEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = Pattern.compile("[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+@[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+\\.[\\w.\\\\+\\-\\*\\/\\=\\`\\~\\!\\#\\$\\%\\^\\&\\*\\{\\}\\|\\'\\_\\?]+")
                .matcher(email);
        return matcher.matches();
    }
}
