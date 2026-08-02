package com.beat.mall.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtil {

    // 匹配文件名中的 宽x高，比如 xxx_800x600.png
    private static final Pattern SIZE_PATTERN = Pattern.compile(".*_(\\d+)x(\\d+)\\..*");

    private static String defaultMaleAvatar = "/photo/avatar.png";
    private static String defaultFeMaleAvatar = "/photo/avatar.png";

    public static String getDefaultMaleAvatar() {
        return defaultMaleAvatar;
    }

    public static String getDefaultFeMaleAvatar() {
        return defaultFeMaleAvatar;
    }

    public static int[] getImageWidthAndHeight(String imageUrl) {
        int[] wh = new int[2];
        String[] imageStr = imageUrl.split("_");
        if (imageStr.length >= 2) {
            String[] imageStrEnd = imageStr[imageStr.length - 1].split("\\.");
            String[] imageParam = imageStrEnd[0].split("x");
            if (imageParam.length == 2 && BaseUtil.isNumeric(imageParam[0]) && BaseUtil.isNumeric(imageParam[1])) {
                wh[0] = Integer.parseInt(imageParam[0]);
                wh[1] = Integer.parseInt(imageParam[1]);
                return wh;
            }
        }
        try {
            URL url = new URL(imageUrl);
            BufferedImage sourceImg = ImageIO.read(new BufferedInputStream(url.openStream()));
            wh[0] = sourceImg.getWidth();
            wh[1] = sourceImg.getHeight();
        } catch (Exception e) {
            wh[0] = 0;
            wh[1] = 0;
        }
        return wh;
    }

    /**
     * 根据文件名计算图片的宽高比（ar）
     *
     * @param filename 文件名
     * @return 宽高比（ar）
     */
    public static Float calcAr(String filename) {
        Matcher matcher = SIZE_PATTERN.matcher(filename);
        if (matcher.matches()) {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));
            if (height > 0) {
                return width * 1.0f / height;
            }
        }
        return 0f;
    }
}
