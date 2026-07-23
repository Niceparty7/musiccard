package top.yuhanpeng.musiccard.app.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.yuhanpeng.musiccard.app.domain.MusicInfoVO;
import top.yuhanpeng.musiccard.app.domain.MusicListFeedVO;
import top.yuhanpeng.musiccard.app.domain.MusicListVO;
import top.yuhanpeng.musiccard.app.domain.MusicListWallImageVO;
import top.yuhanpeng.musiccard.module.entity.Category;
import top.yuhanpeng.musiccard.module.entity.Music;
import top.yuhanpeng.musiccard.module.service.CategoryService;
import top.yuhanpeng.musiccard.module.service.MusicService;
import top.yuhanpeng.musiccard.module.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
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
    public MusicInfoVO getMusicInfoById(@RequestParam(value = "id") Long id) {
        Music music = null;
        Boolean res = true;
        try {
            music = musicService.getById(id);
        } catch (Exception e) {
            log.error("cannot find the id!");
            res = false;
        }
        if (!res) {
            return null;
        }
        Category category = null;
        try {
            category = categoryService.getById((long) music.getTypeId());
        } catch (Exception e) {
            log.error("category cannot be null", e);
        }
        if (category == null) {
            return null;
        }
        List<String> coverImagesString = Arrays.stream(music.getCoverImages().split("\\$")).toList();
        MusicInfoVO musicInfoVO = new MusicInfoVO()
                .setCoverImages(coverImagesString)
                .setMusicName(music.getMusicName())
                .setSingerName(music.getSingerName())
                .setAlbumTitle(music.getAlbumTitle())
                .setReleaseDate(music.getReleaseDate())
                .setMusicDesc(music.getMusicDesc())
                .setTypeName(category.getTypeName())
                .setTypeImage(category.getTypeImage());
        log.info(musicInfoVO.toString());
        return musicInfoVO;
    }

    @RequestMapping("/music/list")
    public MusicListFeedVO getMusicList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        List<MusicListVO> musicCardList = new ArrayList<>();
        Integer pageSize = 10;
        keyword = keyword == null ? keyword : keyword.trim();
        List<Music> list = musicService.getAllMusic(page, pageSize, keyword);
        Boolean isEnd = list.size() < pageSize;
        Category category = null;
        for (Music music : list) {
            try {
                category = categoryService.getById((long) music.getTypeId());
            } catch (Exception e) {
                log.error("category cannot be null", e);
            }
            //该分类不存在，不展示当前音乐
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
        return musicListFeedVO;
    }

    @RequestMapping("/music/download")
    public void download(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("音乐列表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        try {
            musicService.export(response.getOutputStream());
        } catch (Exception e) {
            log.error("下载失败", e);
        }
    }

    @RequestMapping("/music/upload")
    public String upload(MultipartFile file) throws IOException {
        String res = "成功";
        try {
            musicService.upload(file.getInputStream());
        } catch (Exception e) {
            res = "上传失败";
            log.error("上传失败", e);
        }
        return res;
    }

    @RequestMapping("/music/downloadzip")
    public void downloadzip(HttpServletResponse response) throws IOException {
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
    }

    @RequestMapping("/music/uploadzip")
    public String uploadZip(MultipartFile file) throws Exception {
        String res = "批量上传成功";
        try {
            musicService.uploadZip(file);
        } catch (Exception e) {
            res = "批量上传失败";
            log.error("批量上传失败", e);
        }
        return res;
    }
}