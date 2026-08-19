package com.beat.mall.app.feign;

import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "module", contextId = "fileFeign")
public interface FileFeign {
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    Response<String> upload(@RequestPart("file") MultipartFile file);
}
