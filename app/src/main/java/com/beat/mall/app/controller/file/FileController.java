package com.beat.mall.app.controller.file;

import com.beat.mall.module.file.service.FileService;
import com.beat.mall.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class FileController {
    @Autowired
    private FileService fileService;

    @RequestMapping("/upload")
    public Response upload(@RequestParam("file") MultipartFile file) {
        boolean success = true;
        String url = null;
        try {
            url = fileService.uploadAndSave(file);
        } catch (Exception e) {
            success = false;
            log.error("文件上传失败", e);
        }
        if (!success) {
            return new Response<>(4006);
        }
        return new Response<>(1001, url);
    }
}