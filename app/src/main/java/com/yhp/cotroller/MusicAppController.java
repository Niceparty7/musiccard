package com.yhp.cotroller;

import com.yhp.domain.MusicListVO;
import com.yhp.domain.MusicInfoVO;
import com.yhp.domain.MusicListFeedVO;
import com.yhp.entity.Music;
import com.yhp.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MusicAppController {
    @Autowired
    private MusicService musicService;


    @RequestMapping("/music/info")
    public MusicInfoVO getMusicInfoById(@RequestParam("id") Long id) {
        Music music = musicService.getMusicById(id);
        MusicInfoVO musicDetail = new MusicInfoVO();
        musicDetail.setCoverImages(music.getCoverImages());
        musicDetail.setMusicName(music.getMusicName());
        musicDetail.setSingerName(music.getSingerName());
        musicDetail.setAlbumTitle(music.getAlbumTitle());
        musicDetail.setReleaseDate(music.getReleaseDate());
        musicDetail.setMusicDesc(music.getMusicDesc());
        return musicDetail;
    }

    @RequestMapping("/music/list")
    public MusicListFeedVO getMusicList() {
        List<MusicListVO> musicCardList = new ArrayList<>();
        List<Music> list = musicService.getAllMusicInfo();
        for (Music music : list) {
            MusicListVO musicCard = new MusicListVO();
            musicCard.setId(music.getId());
            String[] coverImages = music.getCoverImages().split("\\$");
            musicCard.setWallImage(coverImages[0]);
            musicCard.setMusicName(music.getMusicName());
            musicCard.setSingerName(music.getSingerName());
            musicCard.setMusicDesc(music.getMusicDesc());
            musicCardList.add(musicCard);
        }
        MusicListFeedVO musicListFeedVO = new MusicListFeedVO();
        musicListFeedVO.setList(musicCardList);
        return musicListFeedVO;
    }
}
