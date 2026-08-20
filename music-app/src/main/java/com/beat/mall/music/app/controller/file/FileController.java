package com.beat.mall.music.app.controller.file;

import com.beat.mall.music.app.feign.FileFeign;
import com.beat.mall.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@RestController("appFileController")
@RequiredArgsConstructor
public class FileController {
    private final FileFeign fileFeign;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Response<String> upload(@RequestPart("file") MultipartFile file) {
        return fileFeign.upload(file);
    }
}


