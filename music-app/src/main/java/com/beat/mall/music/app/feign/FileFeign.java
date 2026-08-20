package com.beat.mall.music.app.feign;

import com.beat.mall.music.app.config.AppFeignConfiguration;

import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "music-module", contextId = "appFileFeign", configuration = AppFeignConfiguration.class)
public interface FileFeign {
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    Response<String> upload(@RequestPart("file") MultipartFile file);
}


