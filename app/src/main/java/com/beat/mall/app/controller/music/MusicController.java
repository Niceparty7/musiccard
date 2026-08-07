package com.beat.mall.app.controller.music;

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
    @Autowired
    MusicTagRelationService musicTagRelationService;
    @Autowired
    private MusicService musicService;
    @Autowired
    private BaseMusicService baseMusicService;
    @Autowired
    private CategoryService categoryService;

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
            log.error("cannot find the id!");
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
        List<MusicListVO> musicCardList = new ArrayList<>();
        Integer pageSize = 10;
        keyword = keyword == null ? keyword : keyword.trim();
        List<Music> list = baseMusicService.getAllMusic(page, pageSize, keyword);
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
            if (typeId == null) {
                continue;
            }
            Category category = categoryMap.get((long) typeId);
            if (category == null) {
                continue;
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
        return new Response(1001, musicListFeedVO);
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
        try {
            musicService.export(response.getOutputStream());
        } catch (Exception e) {
            log.error("下载失败", e);
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
        String res = "成功";
        try {
            musicService.upload(file.getInputStream());
        } catch (Exception e) {
            res = "上传失败";
            log.error("上传失败", e);
        }
        return new Response(1001, res);
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
        String res = "批量下载成功";
        File zip = null;
        try {
            zip = musicService.exportZip();
        } catch (Exception e) {
            res = "批量下载失败";
            log.error("批量下载失败", e);
            return new Response(4004);
        }
        Files.copy(zip.toPath(), response.getOutputStream());
        return new Response(1001, res);
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
        String res = "批量上传成功";
        try {
            musicService.uploadZip(file);
        } catch (Exception e) {
            res = "批量上传失败";
            log.error("批量上传失败", e);
            return new Response(4004);
        }
        return new Response(1001, res);
    }
}