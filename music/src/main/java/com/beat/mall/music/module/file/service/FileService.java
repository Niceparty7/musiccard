package com.beat.mall.music.module.file.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.beat.mall.common.entity.file.Type;
import com.beat.mall.common.entity.file.File;
import com.beat.mall.music.module.file.mapper.FileMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class FileService {
    @Autowired
    private FileMapper fileMapper;
    @Value("${oss.bucket}")
    private String bucket;
    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.host}")
    private String host;
    @Value("${oss.dir}")
    private String dir;
    private OSS ossClient;

    @PostConstruct
    public void init() {
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");
        if (accessKeyId == null || accessKeySecret == null) {
            throw new RuntimeException("OSS_ACCESS_KEY_ID or OSS_ACCESS_KEY_SECRET is null");
        }
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    public String uploadAndSave(MultipartFile multipartFile) throws Exception {
        String url = "";
        try {
            url = upload(multipartFile);
        } catch (Exception e) {
            throw new RuntimeException("upload failed", e);
        }
        Type type = determineType(multipartFile.getContentType());
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        File file = new File()
                .setType(type)
                .setUrl(url)
                .setCreateTime(timeStamp)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0);
        fileMapper.insert(file);
        return url;
    }

    private Type determineType(String contentType) {
        if (contentType != null && contentType.startsWith("image/")) {
            return Type.IMAGE;
        } else if (contentType != null && contentType.startsWith("video/")) {
            return Type.VIDEO;
        } else {
            return Type.FILE;
        }
    }

    public String upload(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new RuntimeException("the upload file cannnot be null");
        }

        String originalFileName = file.getOriginalFilename();
        String suffix = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
        String contentType = file.getContentType();

        String folder;
        if (contentType != null && contentType.startsWith("image/")) {
            folder = Type.IMAGE.name().toLowerCase();

        } else if (contentType != null && contentType.startsWith("video/")) {
            folder = Type.VIDEO.name().toLowerCase();

        } else {
            folder = Type.FILE.name().toLowerCase();
        }
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("/yyMM/dd"));
        folder += date;
        Random random = new Random();
        Integer prefix = random.nextInt(1000) + 1;
        String timeStamp = System.currentTimeMillis() + "";
        String newFileName = "";
        String md5Hex = DigestUtil.md5Hex(prefix + timeStamp);
        if (contentType != null && !contentType.startsWith("image/")) {
            newFileName = md5Hex + "." + suffix;
        } else {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            Integer width = bufferedImage.getWidth();
            Integer height = bufferedImage.getHeight();
            newFileName = md5Hex + "_" + width + "x" + height + "." + suffix;
        }
        String key = dir + "/" + folder + "/" + newFileName;
        InputStream inputStream = file.getInputStream();
        ossClient.putObject(bucket, key, inputStream);
        return host + "/" + key;
    }
}
