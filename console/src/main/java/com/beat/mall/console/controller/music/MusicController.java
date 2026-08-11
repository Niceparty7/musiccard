package com.beat.mall.console.controller.music;

import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.domain.music.MusicInfoVO;
import com.beat.mall.console.domain.music.MusicListFeedVO;
import com.beat.mall.console.domain.music.MusicListVO;
import com.beat.mall.module.category.entity.Category;
import com.beat.mall.module.category.service.CategoryService;
import com.beat.mall.module.music.entity.Music;
import com.beat.mall.module.music.service.BaseMusicService;
import com.beat.mall.module.music.service.MusicService;
import com.beat.mall.module.musictagrelation.service.MusicTagRelationService;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.Response;
import cn.hutool.core.io.FileUtil;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class MusicController {
    @Autowired
    private MusicService musicService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private MusicTagRelationService musicTagRelationService;
    @Autowired
    private BaseMusicService baseMusicService;

    @RequestMapping("/music/info")
    public Response getMusicInfoById(@VerifiedUser User loginUser, @RequestParam(value = "id") Long id) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
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
            return new Response<>(3052);
        }
        Category category = null;
        try {
            category = categoryService.getById((long) music.getTypeId());
        } catch (Exception e) {
            log.error("category cannot be null", e);
        }
        if (category == null) {
            return new Response<>(3051);
        }
        List<String> coverImagesString = Arrays.stream(music.getCoverImages().split("\\$")).toList();
        Long createTimeStamp = music.getCreateTime() * 1000L;
        Long updateTimeStamp = music.getUpdateTime() * 1000L;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = simpleDateFormat.format(createTimeStamp);
        String updateTime = simpleDateFormat.format(updateTimeStamp);
        List<String> tags = baseMusicService.getTagsByMusicId(id);
        MusicInfoVO musicInfoVO = new MusicInfoVO()
                .setCoverImages(coverImagesString)
                .setMusicName(music.getMusicName())
                .setSingerName(music.getSingerName())
                .setAlbumTitle(music.getAlbumTitle())
                .setReleaseDate(music.getReleaseDate())
                .setMusicDesc(music.getMusicDesc())
                .setCreateTime(createTime)
                .setUpdateTime(updateTime)
                .setTypeName(category.getTypeName())
                .setTypeImage(category.getTypeImage())
                .setTags(tags);
        log.info(musicInfoVO.toString());
        return new Response<>(1001, musicInfoVO);
    }

    @RequestMapping("/music/list")
    public Response getMusicList(@VerifiedUser User loginUser,
                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "musicName", required = false) String musicName,
                                 @RequestParam(value = "typeName", required = false) String typeName,
                                 @RequestParam(value = "tagName", required = false) String tagName) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        List<MusicListVO> musicCardList = new ArrayList<>();
        Integer pageSize = 10;
        musicName = musicName == null ? musicName : musicName.trim();
        typeName = typeName == null ? typeName : typeName.trim();
        tagName = tagName == null ? tagName : tagName.trim();
        Long total = baseMusicService.countTotal(musicName, typeName, tagName);
        List<Music> list = baseMusicService.getAllMusicList2(page, pageSize, musicName, typeName, tagName);

        Set<Long> typeIds = list.stream()
                .map(Music::getTypeId)
                .filter(tid -> tid != null)
                .map(Integer::longValue)
                .collect(Collectors.toSet());
        Map<Long, String> typeNameMap = new HashMap<>();
        for (Long tid : typeIds) {
            try {
                typeNameMap.put(tid, categoryService.getById(tid).getTypeName());
            } catch (Exception e) {
                log.error("category cannot be null", e);
            }
        }

        for (Music music : list) {
            String[] coverImages = music.getCoverImages().split("\\$");
            Integer tid = music.getTypeId();
            MusicListVO musicListVO = new MusicListVO()
                    .setId(music.getId())
                    .setWallImage(coverImages[0])
                    .setMusicName(music.getMusicName())
                    .setSingerName(music.getSingerName())
                    .setMusicDesc(music.getMusicDesc())
                    .setTypeName(tid != null ? typeNameMap.get((long) tid) : null);
            musicCardList.add(musicListVO);
        }
        MusicListFeedVO musicListFeedVO = new MusicListFeedVO()
                .setList(musicCardList)
                .setTotal(total)
                .setPageSize(pageSize);
        log.info(musicListFeedVO.toString());
        return new Response<>(1001, musicListFeedVO);
    }

    @RequestMapping("/music/create")
    public Response musicCreate(@VerifiedUser User loginUser,
                                @RequestParam(value = "coverImages", required = false) String coverImages,
                                @RequestParam(value = "musicName", required = false) String musicName,
                                @RequestParam(value = "singerName", required = false) String singerName,
                                @RequestParam(value = "musicDesc", required = false) String musicDesc,
                                @RequestParam(value = "albumTitle", required = false) String albumTitle,
                                @RequestParam(value = "releaseDate", required = false) String releaseDate,
                                @RequestParam(value = "typeId", required = false) Integer typeId,
                                @RequestParam(value = "tags", required = false) String tags) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        albumTitle = albumTitle == null ? albumTitle : albumTitle.trim();
        releaseDate = releaseDate == null ? releaseDate : releaseDate.trim();
        boolean success = true;
        Long id = null;
        try {
            id = baseMusicService.edit(null, coverImages, musicName.trim(), singerName.trim(), musicDesc, albumTitle, releaseDate, typeId, tags);
        } catch (Exception e) {
            success = false;
            log.error("create music fail, musicName:{}", musicName, e);
        }
        if (!success) {
            return new Response<>(4005, "创建失败：字段为空或typeId不存在");
        }
        log.info("插入成功,id为{}", id);
        return new Response<>(1001, "插入成功,id为" + id);
    }

    @RequestMapping("/music/update")
    public Response musicUpdate(@VerifiedUser User loginUser,
                                @RequestParam(value = "id") Long id,
                                @RequestParam(value = "coverImages", required = false) String coverImages,
                                @RequestParam(value = "musicName", required = false) String musicName,
                                @RequestParam(value = "singerName", required = false) String singerName,
                                @RequestParam(value = "musicDesc", required = false) String musicDesc,
                                @RequestParam(value = "albumTitle", required = false) String albumTitle,
                                @RequestParam(value = "releaseDate", required = false) String releaseDate,
                                @RequestParam(value = "typeId", required = false) Integer typeId,
                                @RequestParam(value = "tags", required = false) String tags) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        musicName = musicName == null ? musicName : musicName.trim();
        singerName = singerName == null ? singerName : singerName.trim();
        albumTitle = albumTitle == null ? albumTitle : albumTitle.trim();
        releaseDate = releaseDate == null ? releaseDate : releaseDate.trim();
        boolean success = true;
        try {
            baseMusicService.edit(id, coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate, typeId, tags);
        } catch (Exception e) {
            success = false;
            log.error("update music fail, id:{}", id, e);
        }
        if (!success) {
            return new Response<>(4005, "更新失败，id不存在或typeId不存在");
        }
        log.info("更新成功, id:{}", id);
        return new Response<>(1001, "更新成功");
    }

    @RequestMapping("/music/delete")
    public Response musicDelete(@VerifiedUser User loginUser, @RequestParam(value = "id", required = false) Long id) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        boolean success = true;
        Integer affectedRows = 0;
        try {
            affectedRows = musicService.delete(id);
        } catch (Exception e) {
            success = false;
            log.error("delete music fail, id:{}", id, e);
        }
        if (!success || affectedRows != 1) {
            return new Response<>(4005, "删除失败，id为空或不存在");
        }
        log.info("删除成功, id:{}", id);
        return new Response<>(1001, "成功");
    }

    @RequestMapping("/music/download")
    public Response download(@VerifiedUser User loginUser, HttpServletResponse response) throws IOException {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
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
            return new Response<>(4007);
        }
        return new Response<>(1001, "下载成功");
    }

    @RequestMapping("/music/upload")
    public Response upload(@VerifiedUser User loginUser, MultipartFile file) throws IOException {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
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
            return new Response<>(4006);
        }
        return new Response<>(1001, "上传成功");
    }

    @RequestMapping("/music/downloadzip")
    public Response downloadzip(@VerifiedUser User loginUser, HttpServletResponse response) throws IOException {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
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
            return new Response<>(4007, "批量下载失败");
        }
        return new Response<>(1001, "批量下载成功");
    }

    @RequestMapping("/music/uploadzip")
    public Response uploadZip(@VerifiedUser User loginUser, MultipartFile file) throws Exception {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
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
            return new Response<>(4006, "批量上传失败");
        }
        return new Response<>(1001, "批量上传成功");
    }
}