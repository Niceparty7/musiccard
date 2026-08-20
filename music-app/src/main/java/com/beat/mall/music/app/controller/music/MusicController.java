package com.beat.mall.music.app.controller.music;

import com.alibaba.fastjson.JSON;
import com.beat.mall.music.app.annotations.VerifiedUser;
import com.beat.mall.music.app.feign.MusicFeign;
import com.beat.mall.common.api.app.music.MusicInfoVO;
import com.beat.mall.common.api.app.music.MusicListFeedVO;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.common.utils.SignUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@RestController("appMusicController")
@RequiredArgsConstructor
public class MusicController {
    private final MusicFeign musicFeign;

    @GetMapping("/music/info")
    public Response<MusicInfoVO> getMusicInfoById(
            @RequestParam("id") Long id,
            HttpServletRequest request) {
        String sign = getSign(request);
        return SignUtil.parseSign(sign) == null
                ? new Response<>(1002)
                : musicFeign.getMusicInfo(id, sign);
    }

    @GetMapping("/music/list")
    public Response<MusicListFeedVO> getMusicList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return musicFeign.getMusicList(page, keyword);
    }

    @GetMapping("/music/list/demo")
    public Response<MusicListFeedVO> getMusicListDemo(
            @RequestParam(value = "page", defaultValue = "1") Integer page) {
        return musicFeign.getMusicListDemo(page);
    }

    @GetMapping("/music/download")
    public ResponseEntity<byte[]> download(@VerifiedUser User loginUser,
                                            HttpServletRequest request) {
        if (BaseUtil.isEmpty(loginUser)) {
            return unauthorizedBinary();
        }
        return musicFeign.download(getSign(request));
    }

    @PostMapping(value = "/music/upload", consumes = "multipart/form-data")
    public Response<String> upload(@VerifiedUser User loginUser,
                                   @RequestPart("file") MultipartFile file,
                                   HttpServletRequest request) {
        if (BaseUtil.isEmpty(loginUser)) {
            return new Response<>(1002);
        }
        return musicFeign.upload(file, getSign(request));
    }

    @GetMapping("/music/downloadzip")
    public ResponseEntity<byte[]> downloadZip(@VerifiedUser User loginUser,
                                               HttpServletRequest request) {
        if (BaseUtil.isEmpty(loginUser)) {
            return unauthorizedBinary();
        }
        return musicFeign.downloadZip(getSign(request));
    }

    @PostMapping(value = "/music/uploadzip", consumes = "multipart/form-data")
    public Response<String> uploadZip(@VerifiedUser User loginUser,
                                      @RequestPart("file") MultipartFile file,
                                      HttpServletRequest request) {
        if (BaseUtil.isEmpty(loginUser)) {
            return new Response<>(1002);
        }
        return musicFeign.uploadZip(file, getSign(request));
    }

    private String getSign(HttpServletRequest request) {
        String sign = request.getHeader("sign");
        return BaseUtil.isEmpty(sign) ? request.getParameter("sign") : sign;
    }

    private ResponseEntity<byte[]> unauthorizedBinary() {
        byte[] body = JSON.toJSONString(new Response<>(1002))
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}


