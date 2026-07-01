package top.yuhanpeng.musiccard.app.controller;

import top.yuhanpeng.musiccard.app.domain.MusicListFeedVO;
import top.yuhanpeng.musiccard.app.domain.MusicListVO;
import top.yuhanpeng.musiccard.app.domain.MusicInfoVO;
import top.yuhanpeng.musiccard.module.entity.Music;
import top.yuhanpeng.musiccard.module.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class MusicController {
    @Autowired
    private MusicService musicService;

    @RequestMapping("/music/info")
    public MusicInfoVO getMusicInfoById(@RequestParam("id") Long id) {
        Music music = musicService.getMusicById(id);
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
    public MusicListFeedVO getMusicList() {
        List<MusicListVO> musicCardList = new ArrayList<>();
        List<Music> list = musicService.getAllMusicInfo();
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
        musicListFeedVO.setList(musicCardList);
        log.info(musicListFeedVO.toString());
        return musicListFeedVO;
    }
}