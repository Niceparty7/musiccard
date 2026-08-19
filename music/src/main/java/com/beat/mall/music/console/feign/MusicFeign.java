package com.beat.mall.music.console.feign;

import com.beat.mall.music.console.config.ConsoleFeignConfiguration;

import com.beat.mall.common.api.console.music.MusicInfoVO;
import com.beat.mall.common.api.console.music.MusicListFeedVO;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "music", contextId = "consoleMusicFeign", configuration = ConsoleFeignConfiguration.class)
public interface MusicFeign {
    @GetMapping("/music/info")
    Response<MusicInfoVO> getInfo(@RequestHeader("X-User-Id") Long userId,
                                  @RequestParam("id") Long id);

    @GetMapping("/music/list")
    Response<MusicListFeedVO> getList(@RequestHeader("X-User-Id") Long userId,
                                      @RequestParam("page") Integer page,
                                      @RequestParam(value = "musicName", required = false) String musicName,
                                      @RequestParam(value = "typeName", required = false) String typeName,
                                      @RequestParam(value = "tagName", required = false) String tagName);

    @PostMapping("/music/create")
    Response<String> create(@RequestHeader("X-User-Id") Long userId,
                            @RequestParam(value = "coverImages", required = false) String coverImages,
                            @RequestParam(value = "musicName", required = false) String musicName,
                            @RequestParam(value = "singerName", required = false) String singerName,
                            @RequestParam(value = "musicDesc", required = false) String musicDesc,
                            @RequestParam(value = "albumTitle", required = false) String albumTitle,
                            @RequestParam(value = "releaseDate", required = false) String releaseDate,
                            @RequestParam(value = "typeId", required = false) Integer typeId,
                            @RequestParam(value = "tags", required = false) String tags);

    @PutMapping("/music/update")
    Response<String> update(@RequestHeader("X-User-Id") Long userId,
                            @RequestParam("id") Long id,
                            @RequestParam(value = "coverImages", required = false) String coverImages,
                            @RequestParam(value = "musicName", required = false) String musicName,
                            @RequestParam(value = "singerName", required = false) String singerName,
                            @RequestParam(value = "musicDesc", required = false) String musicDesc,
                            @RequestParam(value = "albumTitle", required = false) String albumTitle,
                            @RequestParam(value = "releaseDate", required = false) String releaseDate,
                            @RequestParam(value = "typeId", required = false) Integer typeId,
                            @RequestParam(value = "tags", required = false) String tags);

    @DeleteMapping("/music/delete")
    Response<String> delete(@RequestHeader("X-User-Id") Long userId,
                            @RequestParam("id") Long id);

    @GetMapping("/music/download")
    ResponseEntity<byte[]> download(@RequestHeader("X-User-Id") Long userId);

    @PostMapping(value = "/music/upload", consumes = "multipart/form-data")
    Response<String> upload(@RequestHeader("X-User-Id") Long userId,
                            @RequestPart("file") MultipartFile file);

    @GetMapping("/music/downloadzip")
    ResponseEntity<byte[]> downloadZip(@RequestHeader("X-User-Id") Long userId);

    @PostMapping(value = "/music/uploadzip", consumes = "multipart/form-data")
    Response<String> uploadZip(@RequestHeader("X-User-Id") Long userId,
                               @RequestPart("file") MultipartFile file);
}


