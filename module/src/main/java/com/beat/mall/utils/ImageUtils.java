package com.beat.mall.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUtils {
    public static Float getWallImageAR(String url) throws Exception {
        Pattern pattern = Pattern.compile("_(\\d+)x(\\d+)\\.");
        Matcher matcher = pattern.matcher(url);
        Float ar = null;
        if (matcher.find()) {
            Integer width = Integer.parseInt(matcher.group(1));
            Integer height = Integer.parseInt(matcher.group(2));
            if (height != 0) {
                ar = (float) width / height;
            }
        }
        if (ar == null) {
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(new URL(url));
            } catch (IOException e) {
                throw new RuntimeException("读取图片失败", e);
            }
            if (bufferedImage == null) {
                return 0F;   // 不是图片或下载失败，直接返回默认值
            }
            Integer width = bufferedImage.getWidth();
            Integer height = bufferedImage.getHeight();
            if (height != 0) {
                ar = (float) width / height;
            }
        }
        return ar == null ? 0 : ar;
    }
}