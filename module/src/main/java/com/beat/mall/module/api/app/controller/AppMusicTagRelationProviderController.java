package com.beat.mall.module.api.app.controller;

import com.beat.mall.common.entity.musictagrelation.MusicTagRelation;
import com.beat.mall.module.musictagrelation.service.MusicTagRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(headers = "X-Client-Type=app")
public class AppMusicTagRelationProviderController {
    private final MusicTagRelationService service;

    @RequestMapping("/musicTagRelation/info")
    public MusicTagRelation getDetail(@RequestParam("musicId") Long musicId,
                                      @RequestParam("tagId") Long tagId) {
        return service.getByMusicIdAndTagId(musicId, tagId);
    }

    @RequestMapping("/musicTagRelation/list")
    public List<MusicTagRelation> getAll() {
        return service.getAll();
    }
}
