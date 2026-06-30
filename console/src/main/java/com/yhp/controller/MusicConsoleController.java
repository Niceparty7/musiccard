package com.yhp.controller;

import com.yhp.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MusicConsoleController {
    @Autowired
    private MusicService musicService;


    @RequestMapping("/music/create")
    public String musicCreate(@RequestParam(value = "coverImages") String coverImages, @RequestParam(value = "musicName") String musicName,
                              @RequestParam(value = "singerName") String singerName, @RequestParam(value = "musicDesc") String musicDesc,
                              @RequestParam(value = "albumTitle") String albumTitle, @RequestParam(value = "releaseDate") String releaseDate) {

        int res = musicService.createMusic(coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate);
        return res == 1 ? "成功" : "失败";
    }

    @RequestMapping("/music/update")
    public String musicUpdate(@RequestParam(value = "id") Long id, @RequestParam(value = "coverImages") String coverImages, @RequestParam(value = "musicName") String musicName,
                              @RequestParam(value = "singerName") String singerName, @RequestParam(value = "musicDesc") String musicDesc,
                              @RequestParam(value = "albumTitle") String albumTitle, @RequestParam(value = "releaseDate") String releaseDate) {

        int res = musicService.updateMusic(id, coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate);
        return res == 1 ? "成功" : "失败";
    }

    @RequestMapping("/music/delete")
    public String musicDelete(@RequestParam(value = "id") Long id) {
        int res = musicService.deleteMusic(id);
        return res == 1 ? "成功" : "失败";
    }
}