package com.beat.mall.music.console.controller.file;

import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.music.console.annotations.VerifiedUser;
import com.beat.mall.music.console.feign.FileFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@RestController("consoleFileController")
@RequestMapping(headers = "X-Client-Type=console")
@RequiredArgsConstructor
public class FileController {
    private final FileFeign fileFeign;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Response<String> upload(@VerifiedUser User loginUser,
                                   @RequestPart("file") MultipartFile file) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : fileFeign.upload(loginUser.getId(), file);
    }
}


