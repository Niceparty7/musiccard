package top.yuhanpeng.musiccard.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yuhanpeng.musiccard.app.domain.MusicInfoVO;
import top.yuhanpeng.musiccard.app.domain.MusicListFeedVO;
import top.yuhanpeng.musiccard.app.domain.MusicListVO;
import top.yuhanpeng.musiccard.module.entity.Music;
import top.yuhanpeng.musiccard.module.service.MusicService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class MusicController {
    @Autowired
    private MusicService musicService;

    @RequestMapping("/music/info")
    public MusicInfoVO getMusicInfoById(@RequestParam(value = "id") Long id) {
        Music music = null;
        Boolean res = true;
        try {
            music = musicService.getMusicById(id);
        } catch (Exception e) {
            log.error("cannot find the id!");
            res = false;
        }
        if (!res) {
            return null;
        }
        List<String> coverImagesString = Arrays.stream(music.getCoverImages().split("\\$")).toList();
        MusicInfoVO musicInfoVO = new MusicInfoVO();
        musicInfoVO.setCoverImages(coverImagesString)
                .setMusicName(music.getMusicName())
                .setSingerName(music.getSingerName())
                .setAlbumTitle(music.getAlbumTitle())
                .setReleaseDate(music.getReleaseDate())
                .setMusicDesc(music.getMusicDesc());
        log.info(musicInfoVO.toString());
        return musicInfoVO;
    }

    @RequestMapping("/music/list")
    public MusicListFeedVO getMusicList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        List<MusicListVO> musicCardList = new ArrayList<>();
        Integer pageSize = 10;
        keyword = keyword == null ? keyword : keyword.trim();
        List<Music> list = musicService.getAllMusicInfo(page, pageSize, keyword);
        Boolean isEnd = list.size() < pageSize;
        for (Music music : list) {
            String[] coverImages = music.getCoverImages().split("\\$");
            MusicListVO musicListVO = new MusicListVO();
            musicListVO.setId(music.getId())
                    .setWallImage(coverImages[0])
                    .setMusicName(music.getMusicName())
                    .setSingerName(music.getSingerName())
                    .setMusicDesc(music.getMusicDesc());
            musicCardList.add(musicListVO);
        }
        MusicListFeedVO musicListFeedVO = new MusicListFeedVO();
        musicListFeedVO.setList(musicCardList)
                .setIsEnd(isEnd);
        log.info(musicListFeedVO.toString());
        return musicListFeedVO;
    }
}