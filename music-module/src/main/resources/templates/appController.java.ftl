package com.beat.mall.app.controller;

import ${package.Entity}.${entity};
import ${package.Service}.${entity}Service;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

import java.util.List;

@RestController
@RequestMapping("/${entity?uncap_first}")
public class ${entity}Controller {

    @Resource
    private ${entity}Service service;

    /**
    * 详情接口
    */
    @RequestMapping("/info")
    public ${entity} getDetail(@RequestParam(name = "id") Long id) {
        return service.getById(id);
    }

    /**
    * 列表接口
    */
    @RequestMapping("/list")
    public List<${entity}> getAll() {
        return service.getAll();
    }

}
