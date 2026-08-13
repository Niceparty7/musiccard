package com.beat.mall.app.controller.music;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.beat.mall.app.domain.music.MusicInfoVO;
import com.beat.mall.app.domain.music.MusicListFeedVO;
import com.beat.mall.app.domain.music.MusicListVO;
import com.beat.mall.app.domain.music.MusicListWallImageVO;
import com.beat.mall.module.category.entity.Category;
import com.beat.mall.module.category.service.CategoryService;
import com.beat.mall.module.music.entity.Music;
import com.beat.mall.module.music.service.BaseMusicService;
import com.beat.mall.module.music.service.MusicService;
import com.beat.mall.module.musictagrelation.service.MusicTagRelationService;
import com.beat.mall.utils.ImageUtils;
import com.beat.mall.module.redis.util.RedisUtil;
import com.beat.mall.utils.Response;
import com.beat.mall.utils.SignUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class MusicController {
    private static final String MUSIC_LIST_CACHE_PREFIX = "app:music:list:";
    private static final int MUSIC_LIST_CACHE_TTL_SECONDS = 300;
    @Autowired
    MusicTagRelationService musicTagRelationService;
    @Autowired
    private MusicService musicService;
    @Autowired
    private BaseMusicService baseMusicService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping("/music/info")
    public Response getMusicInfoById(@RequestParam(value = "id") Long id, HttpServletRequest request) {
        String token = request.getHeader("sign");
        if (token == null) {
            log.error("token未获取，未登录");
            return new Response(1002);
        }
        try {
            Long userId = SignUtil.parseSign(token);
        } catch (Exception e) {
            log.error("token解析失败，未登录");
            return new Response(1002);
        }
        Music music = null;
        Boolean res = true;
        try {
            music = musicService.getById(id);
        } catch (Exception e) {
            log.error("cannot find the id, id:{}", id, e);
            res = false;
        }
        if (!res) {
            return new Response(3052);
        }
        Category category = null;
        try {
            category = categoryService.getById((long) music.getTypeId());
        } catch (Exception e) {
            log.error("category cannot be null", e);
        }
        if (category == null) {
            return new Response(3052);
        }
        List<String> coverImagesString = Arrays.stream(music.getCoverImages().split("\\$")).toList();
        List<String> tags = baseMusicService.getTagsByMusicId(id);
        MusicInfoVO musicInfoVO = new MusicInfoVO()
                .setCoverImages(coverImagesString)
                .setMusicName(music.getMusicName())
                .setSingerName(music.getSingerName())
                .setAlbumTitle(music.getAlbumTitle())
                .setReleaseDate(music.getReleaseDate())
                .setMusicDesc(music.getMusicDesc())
                .setTypeName(category.getTypeName())
                .setTypeImage(category.getTypeImage())
                .setTags(tags);
        log.info(musicInfoVO.toString());
        return new Response(1001, musicInfoVO);
    }

    @RequestMapping("/music/list")
    public Response getMusicList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "keyword", required = false) String keyword) {
        keyword = keyword == null ? "" : keyword.trim();
        String cacheKey = MUSIC_LIST_CACHE_PREFIX + page + ":" + keyword;

        // ===== 1. 读缓存：String 类型，整体 JSON =====
        String cachedJson = redisUtil.get(cacheKey);
        if (cachedJson != null) {
            log.info("music/list cache hit, key:{}", cacheKey);
            MusicListFeedVO cachedFeed = JSON.parseObject(cachedJson, MusicListFeedVO.class);
            return new Response(1001, cachedFeed);
        }

        // ===== 2. 缓存未命中，回源 MySQL（原逻辑不变）=====
        List<MusicListVO> musicCardList = new ArrayList<>();
        Integer pageSize = 10;
        String queryKeyword = keyword.isEmpty() ? null : keyword;
        List<Music> list = baseMusicService.getAllMusic(page, pageSize, queryKeyword);
        Boolean isEnd = list.size() < pageSize;

        Set<Long> typeIds = list.stream()
                .map(Music::getTypeId)
                .filter(tid -> tid != null)
                .map(Integer::longValue)
                .collect(Collectors.toSet());
        Map<Long, Category> categoryMap = new HashMap<>();
        for (Long tid : typeIds) {
            try {
                Category c = categoryService.getById(tid);
                categoryMap.put(tid, c);
            } catch (Exception e) {
                log.error("category cannot be null", e);
            }
        }

        for (Music music : list) {
            Integer typeId = music.getTypeId();
            Category category = null;
            if (typeId != null) {
                category = categoryMap.get((long) typeId);
            }
            if (category == null) {
                category = new Category().setTypeName("未知");
            }
            String[] coverImages = music.getCoverImages().split("\\$");
            String wallImageUrl = coverImages[0];
            Float ar = (float) 0;
            try {
                ar = ImageUtils.getWallImageAR(wallImageUrl);
            } catch (Exception e) {
                log.error("cannot get the ar", e);
            }
            MusicListWallImageVO musicListWallImageVO = new MusicListWallImageVO()
                    .setUrl(wallImageUrl)
                    .setAr(ar);
            MusicListVO musicListVO = new MusicListVO()
                    .setId(music.getId())
                    .setWallImage(musicListWallImageVO)
                    .setMusicName(music.getMusicName())
                    .setSingerName(music.getSingerName())
                    .setMusicDesc(music.getMusicDesc())
                    .setTypeName(category.getTypeName());
            musicCardList.add(musicListVO);
        }
        MusicListFeedVO musicListFeedVO = new MusicListFeedVO()
                .setList(musicCardList)
                .setIsEnd(isEnd);
        log.info(musicListFeedVO.toString());

        // ===== 3. 写回缓存，TTL 300s =====
        redisUtil.setex(cacheKey, MUSIC_LIST_CACHE_TTL_SECONDS, JSON.toJSONString(musicListFeedVO));
        return new Response(1001, musicListFeedVO);
    }

    /**
     * List 类型实践演示：全量 RPUSH 为 Redis List，LRANGE 按索引切片分页
     * 用于暴露 List 方案问题：分页索引漂移 / 无法按 keyword 过滤 / 元素级无 TTL / LREM 删除昂贵
     */
    @RequestMapping("/music/list/demo")
    public Response getMusicListDemo(@RequestParam(value = "page", defaultValue = "1") Integer page) {
        Integer pageSize = 10;
        String cacheKey = "app:music:list:demo";

        // 首次访问：回源全量构建 List（RPUSH）
        if (redisUtil.llen(cacheKey) == 0) {
            List<Music> all = baseMusicService.getAllMusic(1, 100000, null);
            List<String> items = all.stream().map(m -> JSON.toJSONString(new MusicListVO()
                            .setId(m.getId())
                            .setMusicName(m.getMusicName())
                            .setSingerName(m.getSingerName())
                            .setMusicDesc(m.getMusicDesc())))
                    .collect(Collectors.toList());
            redisUtil.rpush(cacheKey, items.toArray(new String[0]));
            redisUtil.expire(cacheKey, 300);
        }

        // LRANGE 按索引分页
        long start = (long) (page - 1) * pageSize;
        long end = (long) page * pageSize - 1;
        List<String> pageJson = redisUtil.lrange(cacheKey, start, end);
        List<MusicListVO> list = pageJson.stream()
                .map(s -> JSON.parseObject(s, MusicListVO.class))
                .collect(Collectors.toList());
        boolean isEnd = (long) page * pageSize >= redisUtil.llen(cacheKey);

        MusicListFeedVO feedVO = new MusicListFeedVO().setList(list).setIsEnd(isEnd);
        log.info("music/list/demo page:{}, items:{}, isEnd:{}", page, list.size(), isEnd);
        return new Response(1001, feedVO);
    }


    @RequestMapping("/music/download")
    public Response download(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String token = request.getHeader("sign");
        if (token == null) {
            log.error("token未获取，未登录");
            return new Response(1002);
        }
        try {
            Long userId = SignUtil.parseSign(token);
        } catch (Exception e) {
            log.error("token解析失败，未登录");
            return new Response(1002);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("音乐列表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        boolean success = true;
        try {
            musicService.export(response.getOutputStream());
        } catch (Exception e) {
            success = false;
            log.error("下载失败", e);
        }
        if (!success) {
            return new Response(4007);
        }
        return new Response(1001, "下载成功");
    }

    @RequestMapping("/music/upload")
    public Response upload(MultipartFile file, HttpServletRequest request) throws IOException {
        String token = request.getHeader("sign");
        if (token == null) {
            log.error("token未获取，未登录");
            return new Response(1002);
        }
        try {
            Long userId = SignUtil.parseSign(token);
        } catch (Exception e) {
            log.error("token解析失败，未登录");
            return new Response(1002);
        }
        boolean success = true;
        try {
            musicService.upload(file.getInputStream());
        } catch (Exception e) {
            success = false;
            log.error("上传失败", e);
        }
        if (!success) {
            return new Response(4006);
        }
        return new Response(1001, "上传成功");
    }

    @RequestMapping("/music/downloadzip")
    public Response downloadzip(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String token = request.getHeader("sign");
        if (token == null) {
            log.error("token未获取，未登录");
            return new Response(1002);
        }
        try {
            Long userId = SignUtil.parseSign(token);
        } catch (Exception e) {
            log.error("token解析失败，未登录");
            return new Response(1002);
        }
        response.setContentType("application/zip");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=music.zip");
        boolean success = true;
        File zip = null;
        try {
            zip = musicService.exportZip();
            Files.copy(zip.toPath(), response.getOutputStream());
        } catch (Exception e) {
            success = false;
            log.error("批量下载失败", e);
        } finally {
            // 下载完成后删除 zip 及其父临时目录（含 10 个 xlsx 中间产物）
            if (zip != null) {
                FileUtil.del(zip.getParentFile());
            }
        }
        if (!success) {
            return new Response(4007);
        }
        return new Response(1001, "批量下载成功");
    }

    @RequestMapping("/music/uploadzip")
    public Response uploadZip(MultipartFile file, HttpServletRequest request) throws Exception {
        String token = request.getHeader("sign");
        if (token == null) {
            log.error("token未获取，未登录");
            return new Response(1002);
        }
        try {
            Long userId = SignUtil.parseSign(token);
        } catch (Exception e) {
            log.error("token解析失败，未登录");
            return new Response(1002);
        }
        boolean success = true;
        try {
            musicService.uploadZip(file);
        } catch (Exception e) {
            success = false;
            log.error("批量上传失败", e);
        }
        if (!success) {
            return new Response(4006);
        }
        return new Response(1001, "批量上传成功");
    }
}