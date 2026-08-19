package com.beat.mall.app.controller.musictagrelation;

import com.beat.mall.app.feign.AppMusicTagRelationFeign;
import com.beat.mall.common.entity.musictagrelation.MusicTagRelation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MusicTagRelationController {
    private final AppMusicTagRelationFeign musicTagRelationFeign;

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
