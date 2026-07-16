package top.yuhanpeng.musiccard.module.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

@Service
public class FileService {
    @Value("${file.upload-path}")
    private String uploadPath;

    public String upload(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new RuntimeException("the upload file cannnot be null");
        }

        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
        String contentType = file.getContentType();

        String folder;
        if (contentType != null && contentType.startsWith("image/")) {
            folder = "image";

        } else if (contentType != null && contentType.startsWith("video/")) {
            folder = "video";

        } else {
            folder = "file";
        }
        File dir = new File(uploadPath, folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Random random = new Random();
        Integer prefix = random.nextInt(1000) + 1;
        String timeStamp = System.currentTimeMillis() + "";
        String newFileName = "";
        if (contentType != null && !contentType.startsWith("image/")) {
            newFileName = prefix + timeStamp + "." + suffix;
        } else {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            Integer width = bufferedImage.getWidth();
            Integer height = bufferedImage.getHeight();
            newFileName = prefix + timeStamp + "_" + width + "x" + height + "." + suffix;
        }

        File dest = new File(dir, newFileName);
        file.transferTo(dest);
        return dest.getAbsolutePath();
    }
}