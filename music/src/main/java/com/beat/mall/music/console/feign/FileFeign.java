package com.beat.mall.music.console.feign;

import com.beat.mall.music.console.config.ConsoleFeignConfiguration;

import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "music", contextId = "consoleFileFeign", configuration = ConsoleFeignConfiguration.class)
public interface FileFeign {
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    Response<String> upload(@RequestHeader("X-User-Id") Long userId,
                            @RequestPart("file") MultipartFile file);
}


