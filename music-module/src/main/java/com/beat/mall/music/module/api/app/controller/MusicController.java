package com.beat.mall.music.module.api.app.controller;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.beat.mall.common.api.app.music.*;
import com.beat.mall.common.entity.category.Category;
import com.beat.mall.common.entity.music.Music;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.ImageUtils;
import com.beat.mall.music.module.auth.AuthService;
import com.beat.mall.music.module.category.service.CategoryService;
import com.beat.mall.music.module.music.service.BaseMusicService;
import com.beat.mall.music.module.music.service.MusicSearchWpService;
import com.beat.mall.music.module.music.service.MusicService;
import com.beat.mall.music.module.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController("appMusicController")
@RequiredArgsConstructor
@RequestMapping(headers = {"X-Client-Type=app", "X-Internal-Token"})
public class MusicController {
    private static final String MUSIC_LIST_CACHE_PREFIX = "app:music:list:";
    private static final int MUSIC_LIST_CACHE_TTL_SECONDS = 300;

    private final MusicService musicService;
    private final BaseMusicService baseMusicService;
    private final MusicSearchWpService musicSearchWpService;
    private final CategoryService categoryService;
    private final RedisUtil redisUtil;
    private final AuthService authService;

    @RequestMapping("/music/info")
    public Response<MusicInfoVO> getMusicInfoById(
            @RequestParam("id") Long id,
            @RequestHeader(value = "sign", required = false) String sign) throws Exception {
        authService.requireSign(sign);
        Music music = musicService.getById(id);
        Category category = categoryService.getById((long) music.getTypeId());
        if (category == null) {
            return new Response<>(3052);
        }
        MusicInfoVO vo = new MusicInfoVO()
                .setCoverImages(Arrays.stream(music.getCoverImages().split("\\$")).toList())
                .setMusicName(music.getMusicName())
                .setSingerName(music.getSingerName())
                .setAlbumTitle(music.getAlbumTitle())
                .setReleaseDate(music.getReleaseDate())
                .setMusicDesc(music.getMusicDesc())
                .setTypeName(category.getTypeName())
                .setTypeImage(category.getTypeImage())
                .setTags(baseMusicService.getTagsByMusicId(id));
        return new Response<>(1001, vo);
    }

    @RequestMapping("/music/list")
    public Response<MusicListFeedVO> getMusicList(@RequestParam(value = "keyword", required = false) String keyword,
                                                  @RequestParam(value = "wp", required = false) String wp) {
        MusicSearchWpDTO searchWpDTO = null;
        boolean wpValid = true;
        try {
            if (wp != null && keyword != null) {
                throw new Exception("wp与keyword不能同时传递");
            }
            if (wp != null) {
                searchWpDTO = musicSearchWpService.decode(wp);
            } else {
                searchWpDTO = new MusicSearchWpDTO()
                        .setKeyword(keyword == null ? "" : keyword.trim())
                        .setPage(1);
            }
        } catch (Exception exception) {
            log.warn("Invalid music search wp", exception);
            wpValid = false;
        }
        if (!wpValid) {
            return new Response<>(4009);
        }
        Integer page = searchWpDTO.getPage();
        keyword = searchWpDTO.getKeyword() == null ? "" : searchWpDTO.getKeyword().trim();
        String cacheKey = MUSIC_LIST_CACHE_PREFIX + page + ":" + keyword;
        String cachedJson = redisUtil.get(cacheKey);
        MusicListFeedVO feed;
        if (cachedJson != null) {
            feed = JSON.parseObject(cachedJson, MusicListFeedVO.class);
        } else {
            Integer pageSize = 10;
            List<Music> musicList = baseMusicService.getAllMusic(
                    page, pageSize, keyword.isEmpty() ? null : keyword);
            Set<Long> typeIds = musicList.stream()
                    .map(Music::getTypeId)
                    .filter(typeId -> typeId != null)
                    .map(Integer::longValue)
                    .collect(Collectors.toSet());
            Map<Long, Category> categoryMap = new HashMap<>();
            for (Long typeId : typeIds) {
                try {
                    categoryMap.put(typeId, categoryService.getById(typeId));
                } catch (Exception exception) {
                    log.warn("Cannot load category: {}", typeId, exception);
                }
            }

            List<MusicListVO> result = new ArrayList<>();
            for (Music music : musicList) {
                Category category = music.getTypeId() == null
                        ? null : categoryMap.get(music.getTypeId().longValue());
                if (category == null) {
                    category = new Category().setTypeName("未知");
                }
                String wallImage = music.getCoverImages().split("\\$")[0];
                Float ar;
                try {
                    ar = ImageUtils.getWallImageAR(wallImage);
                } catch (Exception exception) {
                    ar = 0F;
                }
                result.add(new MusicListVO()
                        .setId(music.getId())
                        .setWallImage(new MusicListWallImageVO().setUrl(wallImage).setAr(ar))
                        .setMusicName(music.getMusicName())
                        .setSingerName(music.getSingerName())
                        .setMusicDesc(music.getMusicDesc())
                        .setTypeName(category.getTypeName()));
            }
            feed = new MusicListFeedVO()
                    .setList(result)
                    .setIsEnd(musicList.size() < pageSize)
                    .setWp(null);
            redisUtil.setex(cacheKey, MUSIC_LIST_CACHE_TTL_SECONDS, JSON.toJSONString(feed));
        }
        // 每次响应都重新生成本次“下一页”的 wp
        if (Boolean.TRUE.equals(feed.getIsEnd())) {
            feed.setWp(null);
        } else {
            boolean encodeSuccess = true;
            String nextWp = null;
            try {
                nextWp = musicSearchWpService.encode(
                        new MusicSearchWpDTO()
                                .setKeyword(keyword)
                                .setPage(page + 1)
                );
            } catch (Exception exception) {
                log.error("Generate music search wp failed", exception);
                encodeSuccess = false;
            }
            if (!encodeSuccess) {
                return new Response<>(4005);
            }
            feed.setWp(nextWp);
        }
        return new Response<>(1001, feed);
    }

    @RequestMapping("/music/list/demo")
    public Response<MusicListFeedVO> getMusicListDemo(
            @RequestParam(value = "page", defaultValue = "1") Integer page) {
        int pageSize = 10;
        String cacheKey = "app:music:list:demo";
        if (redisUtil.llen(cacheKey) == 0) {
            List<Music> all = baseMusicService.getAllMusic(1, 100000, null);
            List<String> items = all.stream()
                    .map(music -> JSON.toJSONString(new MusicListVO()
                            .setId(music.getId())
                            .setMusicName(music.getMusicName())
                            .setSingerName(music.getSingerName())
                            .setMusicDesc(music.getMusicDesc())))
                    .toList();
            redisUtil.rpush(cacheKey, items.toArray(new String[0]));
            redisUtil.expire(cacheKey, 300);
        }
        long start = (long) (page - 1) * pageSize;
        long end = (long) page * pageSize - 1;
        List<MusicListVO> list = redisUtil.lrange(cacheKey, start, end).stream()
                .map(item -> JSON.parseObject(item, MusicListVO.class))
                .toList();
        boolean isEnd = (long) page * pageSize >= redisUtil.llen(cacheKey);
        return new Response<>(1001, new MusicListFeedVO().setList(list).setIsEnd(isEnd));
    }

    @RequestMapping("/music/download")
    public ResponseEntity<byte[]> download(
            @RequestHeader(value = "sign", required = false) String sign) throws Exception {
        authService.requireSign(sign);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        musicService.export(outputStream);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=music.xlsx")
                .body(outputStream.toByteArray());
    }

    @RequestMapping("/music/upload")
    public Response<String> upload(
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = "sign", required = false) String sign) throws Exception {
        authService.requireSign(sign);
        musicService.upload(file.getInputStream());
        return new Response<>(1001, "上传成功");
    }

    @RequestMapping("/music/downloadzip")
    public ResponseEntity<byte[]> downloadZip(
            @RequestHeader(value = "sign", required = false) String sign) throws Exception {
        authService.requireSign(sign);
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

    @RequestMapping("/music/uploadzip")
    public Response<String> uploadZip(
            @RequestPart("file") MultipartFile file,
            @RequestHeader(value = "sign", required = false) String sign) throws Exception {
        authService.requireSign(sign);
        musicService.uploadZip(file);
        return new Response<>(1001, "批量上传成功");
    }
}



