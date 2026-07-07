package top.yuhanpeng.musiccard.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
    public MusicInfoVO getMusicInfoById(@RequestParam(value = "id") Long id) {
        Music music = null;
        music = musicService.getById(id);
        if (music == null) {
            return null;
        }
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
    public MusicListFeedVO getMusicList(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        List<MusicListVO> musicCardList = new ArrayList<>();
        Integer pageSize = 10;
        keyword = keyword == null ? keyword : keyword.trim();
        IPage<Music> musicPage = musicService.getAllMusic(page, pageSize, keyword);
        List<Music> list = musicPage.getRecords();
        Long total = musicPage.getTotal();
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
        albumTitle = albumTitle == null ? albumTitle : albumTitle.trim();
        releaseDate = releaseDate == null ? releaseDate : releaseDate.trim();
        Long id = null;
        String res = "";
        try {
            id = musicService.edit(null, coverImages, musicName.trim(), singerName.trim(), musicDesc, albumTitle, releaseDate);
        } catch (Exception e) {
            log.error("coverImages, musicName, singerName cannot be null!", e);
            res = "coverImages, musicName, singerName等字段不能为空！";
        }
        if (id != null) {
            res = "插入成功,id为" + id;
        } else {
            res = "失败 " + res;
        }
        log.info(res);
        return res;
    }

    @RequestMapping("/music/update")
    public String musicUpdate(@RequestParam(value = "id") Long id,
                              @RequestParam(value = "coverImages", required = false) String coverImages,
                              @RequestParam(value = "musicName", required = false) String musicName,
                              @RequestParam(value = "singerName", required = false) String singerName,
                              @RequestParam(value = "musicDesc", required = false) String musicDesc,
                              @RequestParam(value = "albumTitle", required = false) String albumTitle,
                              @RequestParam(value = "releaseDate", required = false) String releaseDate) {
        musicName = musicName == null ? musicName : musicName.trim();
        singerName = singerName == null ? singerName : singerName.trim();
        albumTitle = albumTitle == null ? albumTitle : albumTitle.trim();
        releaseDate = releaseDate == null ? releaseDate : releaseDate.trim();
        String res = "成功";
        try {
            musicService.edit(id, coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate);
        } catch (Exception e) {
            log.error("cannot find the id", e);
            res = "更新失败，id不存在";
        }
        log.info(res);
        return res;
    }

    @RequestMapping("/music/delete")
    public String musicDelete(@RequestParam(value = "id", required = false) Long id) {
        Boolean success = false;
        String res = "";
        try {
            success = musicService.removeById(id);
        } catch (Exception e) {
            log.error("cannot find the id", e);
            res = "id为空";
        }
        if (success) {
            res = "成功";
        } else {
            res = "失败 " + res;
        }
        log.info(res);
        return res;
    }
}