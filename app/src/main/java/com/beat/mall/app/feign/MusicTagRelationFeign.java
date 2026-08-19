package com.beat.mall.app.feign;

import com.beat.mall.common.entity.musictagrelation.MusicTagRelation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "module", contextId = "musicTagRelationFeign")
public interface MusicTagRelationFeign {
    @GetMapping("/musicTagRelation/info")
    MusicTagRelation getDetail(@RequestParam("musicId") Long musicId,
                               @RequestParam("tagId") Long tagId);

    @GetMapping("/musicTagRelation/list")
    List<MusicTagRelation> getAll();
}
