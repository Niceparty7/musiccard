package top.yuhanpeng.musiccard.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yuhanpeng.musiccard.app.domain.MusicInfoVO;
import top.yuhanpeng.musiccard.app.domain.MusicListFeedVO;
import top.yuhanpeng.musiccard.app.domain.MusicListVO;
import top.yuhanpeng.musiccard.app.domain.MusicListWallImageVO;
import top.yuhanpeng.musiccard.module.entity.Category;
import top.yuhanpeng.musiccard.module.entity.Music;
import top.yuhanpeng.musiccard.module.service.CategoryService;
import top.yuhanpeng.musiccard.module.service.MusicService;
import top.yuhanpeng.musiccard.module.utils.ImageUtils;

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
}