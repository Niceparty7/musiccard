package top.yuhanpeng.musiccard.console.controller;

import top.yuhanpeng.musiccard.module.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MusicController {
    @Autowired
    private MusicService musicService;

    @RequestMapping("/music/create")
    public String musicCreate(@RequestParam(value = "coverImages") String coverImages, @RequestParam(value = "musicName") String musicName,
                              @RequestParam(value = "singerName") String singerName, @RequestParam(value = "musicDesc") String musicDesc,
                              @RequestParam(value = "albumTitle") String albumTitle, @RequestParam(value = "releaseDate") String releaseDate) {
        int res = musicService.createMusic(coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate);
        if (res == 1) {
            log.info("音乐新增成功\n");
        } else {
            log.info("音乐新增失败\n");
        }
        return res == 1 ? "成功" : "失败";
    }

    @RequestMapping("/music/update")
    public String musicUpdate(@RequestParam(value = "id") Long id, @RequestParam(value = "coverImages") String coverImages, @RequestParam(value = "musicName") String musicName,
                              @RequestParam(value = "singerName") String singerName, @RequestParam(value = "musicDesc") String musicDesc,
                              @RequestParam(value = "albumTitle") String albumTitle, @RequestParam(value = "releaseDate") String releaseDate) {

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