package com.beat.mall.app.controller.musictagrelation;

import com.beat.mall.module.musictagrelation.entity.MusicTagRelation;
import com.beat.mall.module.musictagrelation.service.MusicTagRelationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/musicTagRelation")
public class MusicTagRelationController {

    @Resource
    private MusicTagRelationService service;

    /**
     * 详情接口
     */
    @RequestMapping("/info")
    public MusicTagRelation getDetail(@RequestParam("musicId") Long musicId, @RequestParam("tagId") Long tagId) {
        return service.getByMusicIdAndTagId(musicId, tagId);
    }

    /**
     * 列表接口
     */
    @RequestMapping("/list")
    public List<MusicTagRelation> getAll() {
        return service.getAll();
    }

}