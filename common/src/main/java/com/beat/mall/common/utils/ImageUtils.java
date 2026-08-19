package com.beat.mall.common.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ImageUtils {
    private ImageUtils() {
    }

    public static Float getWallImageAR(String url) throws Exception {
        Matcher matcher = Pattern.compile("_(\\d+)x(\\d+)\\.").matcher(url);
        Float ar = null;
        if (matcher.find()) {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));
            if (height != 0) {
                ar = (float) width / height;
            }
        }
        if (ar == null) {
            BufferedImage bufferedImage;
            try {
                bufferedImage = ImageIO.read(new URL(url));
            } catch (IOException e) {
                throw new IllegalStateException("读取图片失败", e);
            }
            if (bufferedImage == null) {
                return 0F;
            }
            if (bufferedImage.getHeight() != 0) {
                ar = (float) bufferedImage.getWidth() / bufferedImage.getHeight();
            }
        }
        return ar == null ? 0 : ar;
    }
}
