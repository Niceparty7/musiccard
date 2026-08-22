package com.beat.mall.music.module.api.console.controller;

import com.beat.mall.common.response.Response;
import com.beat.mall.music.module.auth.AuthService;
import com.beat.mall.music.module.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController("consoleFileController")
@RequiredArgsConstructor
@RequestMapping(headers = {"X-Client-Type=console", "X-Internal-Token"})
public class FileController {
    private final FileService fileService;
    private final AuthService providerAuthService;

    @RequestMapping(value = "/upload", headers = "X-Client-Type=console")
    public Response<String> upload(
            @RequestHeader("X-User-Id") Long userId,
            @RequestPart("file") MultipartFile file) {
        providerAuthService.requireUser(userId);
        try {
            return new Response<>(1001, fileService.uploadAndSave(file));
        } catch (Exception exception) {
            log.error("Console file upload failed", exception);
            return new Response<>(4006);
        }
    }
}



