package com.beat.mall.music.app.controller.musictagrelation;

import com.beat.mall.music.app.feign.MusicTagRelationFeign;
import com.beat.mall.common.entity.musictagrelation.MusicTagRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController("appMusicTagRelationController")
@RequestMapping(headers = "X-Client-Type=app")
@RequiredArgsConstructor
public class MusicTagRelationController {
    private final MusicTagRelationFeign musicTagRelationFeign;

    @GetMapping("/musicTagRelation/info")
    public MusicTagRelation getDetail(@RequestParam("musicId") Long musicId,
                                      @RequestParam("tagId") Long tagId) {
        return musicTagRelationFeign.getDetail(musicId, tagId);
    }

    @GetMapping("/musicTagRelation/list")
    public List<MusicTagRelation> getAll() {
        return musicTagRelationFeign.getAll();
    }
}


