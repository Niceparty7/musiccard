package com.beat.mall.console.feign;

import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "module", contextId = "consoleFileFeign")
public interface ConsoleFileFeign {
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    Response<String> upload(@RequestHeader("X-User-Id") Long userId,
                            @RequestPart("file") MultipartFile file);
}
