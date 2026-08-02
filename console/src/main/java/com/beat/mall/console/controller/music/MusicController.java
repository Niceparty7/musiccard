package com.beat.mall.console.controller.music;

import com.beat.mall.console.domain.music.MusicInfoVO;
import com.beat.mall.console.domain.music.MusicListFeedVO;
import com.beat.mall.console.domain.music.MusicListVO;
import com.beat.mall.module.category.entity.Category;
import com.beat.mall.module.category.service.CategoryService;
import com.beat.mall.module.music.domain.MusicListDTO;
import com.beat.mall.module.music.entity.Music;
import com.beat.mall.module.music.service.MusicService;
import com.beat.mall.utils.Response;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class MusicController {
    @Autowired
    private MusicService musicService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/music/info")
    public Response getMusicInfoById(@RequestParam(value = "id") Long id) {
        Music music = null;
        Boolean res = true;
        try {
            music = musicService.getById(id);
        } catch (Exception e) {
            log.error("cannot find the id!");
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
                .setTypeImage(category.getTypeImage());
        log.info(musicInfoVO.toString());
        return new Response<>(1001, musicInfoVO);
    }

    @RequestMapping("/music/list")
    public Response getMusicList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "keyword", required = false) String keyword) {
        List<MusicListVO> musicCardList = new ArrayList<>();
        Integer pageSize = 10;
        keyword = keyword == null ? keyword : keyword.trim();
        Long total = musicService.countTotal(keyword);
        List<MusicListDTO> list = musicService.getAllMusicListDTO(page, pageSize, keyword);
        for (MusicListDTO musicListDTO : list) {
            String[] coverImages = musicListDTO.getCoverImages().split("\\$");
            MusicListVO musicListVO = new MusicListVO()
                    .setId(musicListDTO.getId())
                    .setWallImage(coverImages[0])
                    .setMusicName(musicListDTO.getMusicName())
                    .setSingerName(musicListDTO.getSingerName())
                    .setMusicDesc(musicListDTO.getMusicDesc())
                    .setTypeName(musicListDTO.getTypeName());
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
    public Response musicCreate(@RequestParam(value = "coverImages", required = false) String coverImages,
                                @RequestParam(value = "musicName", required = false) String musicName,
                                @RequestParam(value = "singerName", required = false) String singerName,
                                @RequestParam(value = "musicDesc", required = false) String musicDesc,
                                @RequestParam(value = "albumTitle", required = false) String albumTitle,
                                @RequestParam(value = "releaseDate", required = false) String releaseDate,
                                @RequestParam(value = "typeId", required = false) Integer typeId) {
        albumTitle = albumTitle == null ? albumTitle : albumTitle.trim();
        releaseDate = releaseDate == null ? releaseDate : releaseDate.trim();
        Long id = null;
        String res = "";
        try {
            id = musicService.edit(null, coverImages, musicName.trim(), singerName.trim(), musicDesc, albumTitle, releaseDate, typeId);
        } catch (Exception e) {
            log.error("coverImages, musicName, singerName cannot be null!");
            res = "coverImages, musicName, singerName等字段为空或typeId不存在";
        }
        if (id != null) {
            res = "插入成功,id为" + id;
        } else {
            res = "失败 " + res;
        }
        log.info(res);
        return new Response<>(1001, res);
    }

    @RequestMapping("/music/update")
    public Response musicUpdate(@RequestParam(value = "id") Long id,
                                @RequestParam(value = "coverImages", required = false) String coverImages,
                                @RequestParam(value = "musicName", required = false) String musicName,
                                @RequestParam(value = "singerName", required = false) String singerName,
                                @RequestParam(value = "musicDesc", required = false) String musicDesc,
                                @RequestParam(value = "albumTitle", required = false) String albumTitle,
                                @RequestParam(value = "releaseDate", required = false) String releaseDate,
                                @RequestParam(value = "typeId", required = false) Integer typeId) {
        musicName = musicName == null ? musicName : musicName.trim();
        singerName = singerName == null ? singerName : singerName.trim();
        albumTitle = albumTitle == null ? albumTitle : albumTitle.trim();
        releaseDate = releaseDate == null ? releaseDate : releaseDate.trim();
        String res = "成功";
        try {
            musicService.edit(id, coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate, typeId);
        } catch (Exception e) {
            log.error("cannot find the id");
            res = "更新失败，id不存在或typeId不存在";
        }
        log.info(res);
        return new Response<>(1001, res);
    }

    @RequestMapping("/music/delete")
    public Response musicDelete(@RequestParam(value = "id", required = false) Long id) {
        Integer affectedRows = 0;
        String res = "";
        try {
            affectedRows = musicService.delete(id);
        } catch (Exception e) {
            log.error("cannot find the id");
            res = "id为空";
        }
        if (affectedRows == 1) {
            res = "成功";
        } else {
            res = "失败 " + res;
        }
        log.info(res);
        return new Response<>(1001, res);
    }

    @RequestMapping("/music/download")
    public Response download(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("音乐列表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        String res = "下载成功";
        try {
            musicService.export(response.getOutputStream());
        } catch (Exception e) {
            res = "下载失败";
            log.error("下载失败", e);
        }
        return new Response<>(1001, res);
    }

    @RequestMapping("/music/upload")
    public Response upload(MultipartFile file) throws IOException {
        String res = "成功";
        try {
            musicService.upload(file.getInputStream());
        } catch (Exception e) {
            res = "上传失败";
            log.error("上传失败", e);
        }
        return new Response<>(1001, res);
    }

    @RequestMapping("/music/downloadzip")
    public Response downloadzip(HttpServletResponse response) throws IOException {
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
        }
        Files.copy(zip.toPath(), response.getOutputStream());
        return new Response<>(1001, res);
    }

    @RequestMapping("/music/uploadzip")
    public Response uploadZip(MultipartFile file) throws Exception {
        String res = "批量上传成功";
        try {
            musicService.uploadZip(file);
        } catch (Exception e) {
            res = "批量上传失败";
            log.error("批量上传失败", e);
        }
        return new Response<>(1001, res);
    }
}