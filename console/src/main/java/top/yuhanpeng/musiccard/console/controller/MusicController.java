package top.yuhanpeng.musiccard.console.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yuhanpeng.musiccard.console.domain.MusicInfoVO;
import top.yuhanpeng.musiccard.console.domain.MusicListFeedVO;
import top.yuhanpeng.musiccard.console.domain.MusicListVO;
import top.yuhanpeng.musiccard.module.entity.Music;
import top.yuhanpeng.musiccard.module.service.MusicService;

import java.text.SimpleDateFormat;
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
        Long createTimeStamp = music.getCreateTime() * 1000L;
        Long updateTimeStamp = music.getUpdateTime() * 1000L;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = simpleDateFormat.format(createTimeStamp);
        String updateTime = simpleDateFormat.format(updateTimeStamp);
        MusicInfoVO musicInfoVO = new MusicInfoVO();
        musicInfoVO.setCoverImages(coverImagesString)
                .setMusicName(music.getMusicName())
                .setSingerName(music.getSingerName())
                .setAlbumTitle(music.getAlbumTitle())
                .setReleaseDate(music.getReleaseDate())
                .setMusicDesc(music.getMusicDesc())
                .setCreateTime(createTime)
                .setUpdateTime(updateTime);
        log.info(musicInfoVO.toString());
        return musicInfoVO;
    }

    @RequestMapping("/music/list")
    public MusicListFeedVO getMusicList(@RequestParam(value = "page", defaultValue = "1") Integer page) {
        List<MusicListVO> musicCardList = new ArrayList<>();
        Long total = musicService.getTotal();
        Integer pageSize = 10;
        List<Music> list = musicService.getAllMusicInfo(page, pageSize);
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
                .setTotal(total)
                .setPageSize(pageSize);
        log.info(musicListFeedVO.toString());
        return musicListFeedVO;
    }

    @RequestMapping("/music/create")
    public String musicCreate(@RequestParam(value = "coverImages", required = false) String coverImages,
                              @RequestParam(value = "musicName", required = false) String musicName,
                              @RequestParam(value = "singerName", required = false) String singerName,
                              @RequestParam(value = "musicDesc", required = false) String musicDesc,
                              @RequestParam(value = "albumTitle", required = false) String albumTitle,
                              @RequestParam(value = "releaseDate", required = false) String releaseDate) {
        Long id = musicService.createMusic(coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate);
        if (id != null) {
            log.info("音乐新增成功,id={}\n", id);
        } else {
            log.info("音乐新增失败\n");
        }
        return id != null ? "插入成功,id为" + id : "失败";
    }

    @RequestMapping("/music/update")
    public String musicUpdate(@RequestParam(value = "id") Long id,
                              @RequestParam(value = "coverImages", required = false) String coverImages,
                              @RequestParam(value = "musicName", required = false) String musicName,
                              @RequestParam(value = "singerName", required = false) String singerName,
                              @RequestParam(value = "musicDesc", required = false) String musicDesc,
                              @RequestParam(value = "albumTitle", required = false) String albumTitle,
                              @RequestParam(value = "releaseDate", required = false) String releaseDate) {
        int res = musicService.updateMusic(id, coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate);
        if (res == 1) {
            log.info("音乐更新成功\n");
        } else {
            log.info("音乐更新失败\n");
        }
        return res == 1 ? "成功" : "失败";
    }

    @RequestMapping("/music/delete")
    public String musicDelete(@RequestParam(value = "id") Long id) {
        int res = musicService.deleteMusic(id);
        if (res == 1) {
            log.info("音乐删除成功\n");
        } else {
            log.info("音乐删除失败\n");
        }
        return res == 1 ? "成功" : "失败";
    }
}