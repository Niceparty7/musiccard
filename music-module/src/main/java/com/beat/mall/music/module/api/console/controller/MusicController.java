package com.beat.mall.music.module.api.console.controller;

import cn.hutool.core.io.FileUtil;
import com.beat.mall.common.api.console.music.MusicInfoVO;
import com.beat.mall.common.api.console.music.MusicListFeedVO;
import com.beat.mall.common.api.console.music.MusicListVO;
import com.beat.mall.common.entity.category.Category;
import com.beat.mall.common.entity.music.Music;
import com.beat.mall.common.response.Response;
import com.beat.mall.music.module.auth.AuthService;
import com.beat.mall.music.module.category.service.CategoryService;
import com.beat.mall.music.module.music.service.BaseMusicService;
import com.beat.mall.music.module.music.service.MusicService;
import com.beat.mall.music.module.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController("consoleMusicController")
@RequiredArgsConstructor
@RequestMapping(headers = {"X-Client-Type=console", "X-Internal-Token"})
public class MusicController {
    private final MusicService musicService;
    private final CategoryService categoryService;
    private final BaseMusicService baseMusicService;
    private final RedisUtil redisUtil;
    private final AuthService providerAuthService;

    @RequestMapping(value = "/music/info", headers = "X-Client-Type=console")
    public Response<MusicInfoVO> getMusicInfo(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long id) throws Exception {
        providerAuthService.requireUser(userId);
        Music music = musicService.getById(id);
        Integer typeId = music.getTypeId();
        Category category = typeId == null ? null : categoryService.getById(typeId.longValue());
        if (typeId != null && category == null) {
            return new Response<>(3051);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MusicInfoVO result = new MusicInfoVO()
                .setCoverImages(Arrays.stream(music.getCoverImages().split("\\$")).toList())
                .setMusicName(music.getMusicName())
                .setSingerName(music.getSingerName())
                .setAlbumTitle(music.getAlbumTitle())
                .setReleaseDate(music.getReleaseDate())
                .setMusicDesc(music.getMusicDesc())
                .setCreateTime(format.format(music.getCreateTime() * 1000L))
                .setUpdateTime(format.format(music.getUpdateTime() * 1000L))
                .setTypeId(typeId)
                .setTypeName(category == null ? null : category.getTypeName())
                .setTypeImage(category == null ? null : category.getTypeImage())
                .setTags(baseMusicService.getTagsByMusicId(id));
        return new Response<>(1001, result);
    }

    @RequestMapping(value = "/music/list", headers = "X-Client-Type=console")
    public Response<MusicListFeedVO> getMusicList(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(required = false) String musicName,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false) String tagName) throws Exception {
        providerAuthService.requireUser(userId);
        int pageSize = 10;
        musicName = trim(musicName);
        typeName = trim(typeName);
        tagName = trim(tagName);
        Long total = baseMusicService.countTotal(musicName, typeName, tagName);
        List<Music> musicList = baseMusicService.getAllMusicList2(
                page, pageSize, musicName, typeName, tagName);
        Map<Long, String> typeNameMap = new HashMap<>();
        Set<Long> typeIds = musicList.stream()
                .map(Music::getTypeId)
                .filter(typeId -> typeId != null)
                .map(Integer::longValue)
                .collect(Collectors.toSet());
        for (Long typeId : typeIds) {
            Category category = categoryService.getById(typeId);
            if (category != null) {
                typeNameMap.put(typeId, category.getTypeName());
            }
        }
        List<MusicListVO> list = new ArrayList<>();
        for (Music music : musicList) {
            list.add(new MusicListVO()
                    .setId(music.getId())
                    .setWallImage(music.getCoverImages().split("\\$")[0])
                    .setMusicName(music.getMusicName())
                    .setSingerName(music.getSingerName())
                    .setMusicDesc(music.getMusicDesc())
                    .setTypeName(music.getTypeId() == null
                            ? null : typeNameMap.get(music.getTypeId().longValue())));
        }
        return new Response<>(1001, new MusicListFeedVO()
                .setList(list)
                .setTotal(total)
                .setPageSize(pageSize));
    }

    @RequestMapping(value = "/music/create", headers = "X-Client-Type=console")
    public Response<String> create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String coverImages,
            @RequestParam(required = false) String musicName,
            @RequestParam(required = false) String singerName,
            @RequestParam(required = false) String musicDesc,
            @RequestParam(required = false) String albumTitle,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) String tags) throws Exception {
        providerAuthService.requireUser(userId);
        Long id = baseMusicService.edit(null, coverImages,
                requiredTrim(musicName), requiredTrim(singerName), musicDesc,
                trim(albumTitle), trim(releaseDate), typeId, tags);
        redisUtil.delByPrefix("app:music:list:");
        return new Response<>(1001, "插入成功,id为" + id);
    }

    @RequestMapping(value = "/music/update", headers = "X-Client-Type=console")
    public Response<String> update(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long id,
            @RequestParam(required = false) String coverImages,
            @RequestParam(required = false) String musicName,
            @RequestParam(required = false) String singerName,
            @RequestParam(required = false) String musicDesc,
            @RequestParam(required = false) String albumTitle,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) Integer typeId,
            @RequestParam(required = false) String tags) throws Exception {
        providerAuthService.requireUser(userId);
        baseMusicService.edit(id, coverImages, trim(musicName), trim(singerName), musicDesc,
                trim(albumTitle), trim(releaseDate), typeId, tags);
        redisUtil.delByPrefix("app:music:list:");
        return new Response<>(1001, "更新成功");
    }

    @RequestMapping(value = "/music/delete", headers = "X-Client-Type=console")
    public Response<String> delete(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long id) throws Exception {
        providerAuthService.requireUser(userId);
        Integer affectedRows = musicService.delete(id);
        if (affectedRows == null || affectedRows != 1) {
            return new Response<>(4005, "删除失败，id为空或不存在");
        }
        redisUtil.delByPrefix("app:music:list:");
        return new Response<>(1001, "成功");
    }

    @RequestMapping(value = "/music/download", headers = "X-Client-Type=console")
    public ResponseEntity<byte[]> download(
            @RequestHeader("X-User-Id") Long userId) throws Exception {
        providerAuthService.requireUser(userId);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        musicService.export(outputStream);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=music.xlsx")
                .body(outputStream.toByteArray());
    }

    @RequestMapping(value = "/music/upload", headers = "X-Client-Type=console")
    public Response<String> upload(
            @RequestHeader("X-User-Id") Long userId,
            @RequestPart("file") MultipartFile file) throws Exception {
        providerAuthService.requireUser(userId);
        musicService.upload(file.getInputStream());
        return new Response<>(1001, "上传成功");
    }

    @RequestMapping(value = "/music/downloadzip", headers = "X-Client-Type=console")
    public ResponseEntity<byte[]> downloadZip(
            @RequestHeader("X-User-Id") Long userId) throws Exception {
        providerAuthService.requireUser(userId);
        File zip = musicService.exportZip();
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=music.zip")
                    .body(Files.readAllBytes(zip.toPath()));
        } finally {
            FileUtil.del(zip.getParentFile());
        }
    }

    @RequestMapping(value = "/music/uploadzip", headers = "X-Client-Type=console")
    public Response<String> uploadZip(
            @RequestHeader("X-User-Id") Long userId,
            @RequestPart("file") MultipartFile file) throws Exception {
        providerAuthService.requireUser(userId);
        musicService.uploadZip(file);
        return new Response<>(1001, "批量上传成功");
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private String requiredTrim(String value) {
        return value == null ? null : value.trim();
    }
}



