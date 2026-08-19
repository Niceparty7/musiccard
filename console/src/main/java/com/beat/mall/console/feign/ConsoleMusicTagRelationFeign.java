package com.beat.mall.console.feign;

import com.beat.mall.common.entity.musictagrelation.MusicTagRelation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "module", contextId = "consoleMusicTagRelationFeign")
public interface ConsoleMusicTagRelationFeign {
    @GetMapping("/musicTagRelation/info")
    MusicTagRelation getDetail(@RequestHeader("X-User-Id") Long userId,
                               @RequestParam("musicId") Long musicId,
                               @RequestParam("tagId") Long tagId);

    @GetMapping("/musicTagRelation/list")
    List<MusicTagRelation> getAll(@RequestHeader("X-User-Id") Long userId);

    @PostMapping("/musicTagRelation/create")
    void create(@RequestHeader("X-User-Id") Long userId,
                @RequestBody MusicTagRelation entity);

    @PutMapping("/musicTagRelation/update")
    void update(@RequestHeader("X-User-Id") Long userId,
                @RequestBody MusicTagRelation entity);

    @DeleteMapping("/musicTagRelation/delete")
    void delete(@RequestHeader("X-User-Id") Long userId,
                @RequestParam("musicId") Long musicId,
                @RequestParam("tagId") Long tagId);
}
