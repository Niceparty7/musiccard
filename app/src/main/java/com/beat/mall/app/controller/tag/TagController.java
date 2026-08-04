package com.beat.mall.app.controller.tag;

import com.beat.mall.module.tag.entity.Tag;
import com.beat.mall.module.tag.service.TagService;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

import java.util.List;

@RestController
@RequestMapping("/tag")
public class TagController {

    @Resource
    private TagService service;

    /**
    * 详情接口
    */
    @RequestMapping("/info")
    public Tag getDetail(@RequestParam(name = "id") Long id) {
        return service.getById(id);
    }

    /**
    * 列表接口
    */
    @RequestMapping("/list")
    public List<Tag> getAll() {
        return service.getAll();
    }

}