package com.beat.mall.music.app.feign;

import com.beat.mall.music.app.config.AppFeignConfiguration;

import com.beat.mall.common.entity.tag.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "music", contextId = "appTagFeign", configuration = AppFeignConfiguration.class)
public interface TagFeign {
    @GetMapping("/tag/info")
    Tag getDetail(@RequestParam("id") Long id);

    @GetMapping("/tag/list")
    List<Tag> getAll();
}


