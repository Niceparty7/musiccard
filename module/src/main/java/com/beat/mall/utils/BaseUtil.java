package com.beat.mall.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class BaseUtil {

    /**
     * md5加密
     *
     * @param text 待加密字符串
     * @return
     */
    public static String md5(String text) {
        String encodeStr = "";
        try {
            encodeStr = DigestUtils.md5Hex(text);
        } catch (Exception e) {
            log.error("Md5 error.", e.getMessage());
            return encodeStr;
        }
        return encodeStr;
    }

    public static int currentSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 判断参数是否为空
     *
     * @param obj 校验的对象
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj instanceof List) {
            return obj == null || ((List<?>) obj).size() == 0;
        } else if (obj instanceof Number) {
            DecimalFormat decimalFormat = new DecimalFormat();
            try {
                return decimalFormat.parse(obj.toString()).doubleValue() == 0;
            } catch (ParseException e) {
                log.error(e.getMessage(), e);
                return false;
            }
        } else {
            return obj == null || "".equals(obj.toString());
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
}
