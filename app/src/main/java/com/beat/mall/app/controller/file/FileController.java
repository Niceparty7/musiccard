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
        String res = "上传成功";
        try {
            res = fileService.uploadAndSave(file);
        } catch (Exception e) {
            res = "文件上传失败";
            log.error("文件上传失败", e);
        }
        return new Response<>(1001,res);
    }
}