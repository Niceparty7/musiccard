package com.beat.mall.console.controller;

import ${package.Entity}.${entity};
import ${package.Service}.${entity}Service;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    /**
    * 新增接口
    */
    @PostMapping("/create")
    public void create(@RequestBody ${entity} entity) {
        service.insert(entity);
    }

    /**
    * 更新接口
    */
    @PutMapping("/update")
    public void update(@RequestBody ${entity} entity) {
        service.update(entity);
    }

    /**
    * 删除接口
    */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}