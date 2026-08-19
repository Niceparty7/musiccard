package com.beat.mall.console.controller.music;

import com.alibaba.fastjson.JSON;
import com.beat.mall.common.api.console.music.MusicInfoVO;
import com.beat.mall.common.api.console.music.MusicListFeedVO;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.feign.ConsoleMusicFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class MusicController {
    private final ConsoleMusicFeign musicFeign;

    @GetMapping("/music/info")
    public Response<MusicInfoVO> getInfo(@VerifiedUser User loginUser,
                                         @RequestParam Long id) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002) : musicFeign.getInfo(loginUser.getId(), id);
    }

    @GetMapping("/music/list")
    public Response<MusicListFeedVO> getList(@VerifiedUser User loginUser,
                                             @RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(required = false) String musicName,
                                             @RequestParam(required = false) String typeName,
                                             @RequestParam(required = false) String tagName) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : musicFeign.getList(loginUser.getId(), page, musicName, typeName, tagName);
    }

    @PostMapping("/music/create")
    public Response<String> create(@VerifiedUser User loginUser,
                                   @RequestParam(required = false) String coverImages,
                                   @RequestParam(required = false) String musicName,
                                   @RequestParam(required = false) String singerName,
                                   @RequestParam(required = false) String musicDesc,
                                   @RequestParam(required = false) String albumTitle,
                                   @RequestParam(required = false) String releaseDate,
                                   @RequestParam(required = false) Integer typeId,
                                   @RequestParam(required = false) String tags) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : musicFeign.create(loginUser.getId(), coverImages, musicName, singerName,
                musicDesc, albumTitle, releaseDate, typeId, tags);
    }

    @PutMapping("/music/update")
    public Response<String> update(@VerifiedUser User loginUser,
                                   @RequestParam Long id,
                                   @RequestParam(required = false) String coverImages,
                                   @RequestParam(required = false) String musicName,
                                   @RequestParam(required = false) String singerName,
                                   @RequestParam(required = false) String musicDesc,
                                   @RequestParam(required = false) String albumTitle,
                                   @RequestParam(required = false) String releaseDate,
                                   @RequestParam(required = false) Integer typeId,
                                   @RequestParam(required = false) String tags) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : musicFeign.update(loginUser.getId(), id, coverImages, musicName, singerName,
                musicDesc, albumTitle, releaseDate, typeId, tags);
    }

    @DeleteMapping("/music/delete")
    public Response<String> delete(@VerifiedUser User loginUser,
                                   @RequestParam Long id) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002) : musicFeign.delete(loginUser.getId(), id);
    }

    @GetMapping("/music/download")
    public ResponseEntity<byte[]> download(@VerifiedUser User loginUser) {
        return BaseUtil.isEmpty(loginUser)
                ? unauthorizedBinary()
                : musicFeign.download(loginUser.getId());
    }

    @PostMapping(value = "/music/upload", consumes = "multipart/form-data")
    public Response<String> upload(@VerifiedUser User loginUser,
                                   @RequestPart("file") MultipartFile file) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : musicFeign.upload(loginUser.getId(), file);
    }

    @GetMapping("/music/downloadzip")
    public ResponseEntity<byte[]> downloadZip(@VerifiedUser User loginUser) {
        return BaseUtil.isEmpty(loginUser)
                ? unauthorizedBinary()
                : musicFeign.downloadZip(loginUser.getId());
    }

    @PostMapping(value = "/music/uploadzip", consumes = "multipart/form-data")
    public Response<String> uploadZip(@VerifiedUser User loginUser,
                                      @RequestPart("file") MultipartFile file) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : musicFeign.uploadZip(loginUser.getId(), file);
    }

    private ResponseEntity<byte[]> unauthorizedBinary() {
        byte[] body = JSON.toJSONString(new Response<>(1002))
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
