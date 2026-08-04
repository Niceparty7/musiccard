package com.beat.mall.console.controller.musictagrelation;

import com.beat.mall.module.musictagrelation.entity.MusicTagRelation;
import com.beat.mall.module.musictagrelation.service.MusicTagRelationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 新增接口
     */
    @PostMapping("/create")
    public void create(@RequestBody MusicTagRelation entity) {
        service.insert(entity);
    }

    /**
     * 更新接口
     */
    @PutMapping("/update")
    public void update(@RequestBody MusicTagRelation entity) {
        service.update(entity);
    }

    /**
     * 删除接口
     */
    @DeleteMapping("/delete")
    public void delete(@RequestParam("musicId") Long musicId, @RequestParam("tagId") Long tagId) {
        service.delete(musicId, tagId);
    }

}