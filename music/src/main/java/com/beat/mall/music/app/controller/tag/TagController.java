package com.beat.mall.music.app.controller.tag;

import com.beat.mall.music.app.feign.TagFeign;
import com.beat.mall.common.entity.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RestController("appTagController")
@RequestMapping(headers = "X-Client-Type=app")
@RequiredArgsConstructor
public class TagController {
    private final TagFeign tagFeign;

    @GetMapping("/tag/info")
    public Tag getDetail(@RequestParam("id") Long id) {
        return tagFeign.getDetail(id);
    }

    @GetMapping("/tag/list")
    public List<Tag> getAll() {
        return tagFeign.getAll();
    }
}


