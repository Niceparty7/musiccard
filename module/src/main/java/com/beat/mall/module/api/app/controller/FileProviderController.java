package com.beat.mall.module.api.app.controller;

import com.beat.mall.common.response.Response;
import com.beat.mall.module.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController("appFileProviderController")
@RequiredArgsConstructor
@RequestMapping(headers = "X-Client-Type=app")
public class FileProviderController {
    private final FileService fileService;

    @RequestMapping("/upload")
    public Response<String> upload(@RequestPart("file") MultipartFile file) {
        try {
            return new Response<>(1001, fileService.uploadAndSave(file));
        } catch (Exception exception) {
            log.error("App file upload failed", exception);
            return new Response<>(4006);
        }
    }
}
