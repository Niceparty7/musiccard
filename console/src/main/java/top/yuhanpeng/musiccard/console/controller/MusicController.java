package top.yuhanpeng.musiccard.console.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yuhanpeng.musiccard.module.service.MusicService;

@Slf4j
@RestController
public class MusicController {
    @Autowired
    private MusicService musicService;

    @RequestMapping("/music/create")
    public String musicCreate(@RequestParam(value = "coverImages") String coverImages, @RequestParam(value = "musicName") String musicName,
                              @RequestParam(value = "singerName") String singerName, @RequestParam(value = "musicDesc") String musicDesc,
                              @RequestParam(value = "albumTitle") String albumTitle, @RequestParam(value = "releaseDate") String releaseDate) {
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