package com.beat.mall.console.controller.file;

import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.module.file.service.FileService;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.utils.BaseUtil;
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
    public Response upload(@VerifiedUser User loginUser, @RequestParam("file") MultipartFile file) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
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