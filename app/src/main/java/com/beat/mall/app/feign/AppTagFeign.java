package com.beat.mall.app.feign;

import com.beat.mall.common.entity.tag.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "module", contextId = "appTagFeign")
public interface AppTagFeign {
    @GetMapping("/tag/info")
    Tag getDetail(@RequestParam("id") Long id);

    @GetMapping("/tag/list")
    List<Tag> getAll();
}
