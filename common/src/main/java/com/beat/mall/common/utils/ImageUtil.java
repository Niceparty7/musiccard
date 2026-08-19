package com.beat.mall.common.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.net.URL;

public final class ImageUtil {
    private ImageUtil() {
    }

    public static String getDefaultMaleAvatar() {
        return "/photo/avatar.png";
    }

    public static String getDefaultFeMaleAvatar() {
        return "/photo/avatar.png";
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
            BufferedImage sourceImg = ImageIO.read(new BufferedInputStream(new URL(imageUrl).openStream()));
            wh[0] = sourceImg.getWidth();
            wh[1] = sourceImg.getHeight();
        } catch (Exception ignored) {
            // 图片不可读时返回 0,0，保持原有业务兼容行为。
        }
        return wh;
    }

    public static Float calcAr(String filename) {
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile(".*_(\\d+)x(\\d+)\\..*").matcher(filename);
        if (matcher.matches()) {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));
            return height > 0 ? width * 1.0f / height : 0f;
        }
        return 0f;
    }
}
