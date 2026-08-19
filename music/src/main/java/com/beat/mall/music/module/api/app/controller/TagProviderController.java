package com.beat.mall.music.module.api.app.controller;

import com.beat.mall.common.entity.tag.Tag;
import com.beat.mall.music.module.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("appTagProviderController")
@RequiredArgsConstructor
@RequestMapping(headers = {"X-Client-Type=app", "X-Internal-Token"})
public class TagProviderController {
    private final TagService service;

    @RequestMapping("/tag/info")
    public Tag getDetail(@RequestParam("id") Long id) {
        return service.getById(id);
    }

    @RequestMapping("/tag/list")
    public List<Tag> getAll() {
        return service.getAll();
    }
}



