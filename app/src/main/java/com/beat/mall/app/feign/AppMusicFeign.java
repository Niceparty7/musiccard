package com.beat.mall.app.feign;

import com.beat.mall.common.api.app.music.MusicInfoVO;
import com.beat.mall.common.api.app.music.MusicListFeedVO;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "module", contextId = "appMusicFeign")
public interface AppMusicFeign {
    @GetMapping("/music/info")
    Response<MusicInfoVO> getMusicInfo(@RequestParam("id") Long id,
                                        @RequestHeader("sign") String sign);

    @GetMapping("/music/list")
    Response<MusicListFeedVO> getMusicList(
            @RequestParam("page") Integer page,
            @RequestParam(value = "keyword", required = false) String keyword);

    @GetMapping("/music/list/demo")
    Response<MusicListFeedVO> getMusicListDemo(@RequestParam("page") Integer page);

    @GetMapping("/music/download")
    ResponseEntity<byte[]> download(@RequestHeader(value = "sign", required = false) String sign);

    @PostMapping(value = "/music/upload", consumes = "multipart/form-data")
    Response<String> upload(@RequestPart("file") MultipartFile file,
                            @RequestHeader(value = "sign", required = false) String sign);

    @GetMapping("/music/downloadzip")
    ResponseEntity<byte[]> downloadZip(@RequestHeader(value = "sign", required = false) String sign);

    @PostMapping(value = "/music/uploadzip", consumes = "multipart/form-data")
    Response<String> uploadZip(@RequestPart("file") MultipartFile file,
                               @RequestHeader(value = "sign", required = false) String sign);
}
