package com.beat.mall.music.console.feign;

import com.beat.mall.music.console.config.ConsoleFeignConfiguration;

import com.beat.mall.common.api.console.category.CategoryInfoVO;
import com.beat.mall.common.api.console.category.CategoryListFeedVO;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "music-module", contextId = "consoleCategoryFeign", configuration = ConsoleFeignConfiguration.class)
public interface CategoryFeign {
    @GetMapping("/category/list")
    Response<CategoryListFeedVO> getList(@RequestHeader("X-User-Id") Long userId);

    @GetMapping("/category/info")
    Response<CategoryInfoVO> getInfo(@RequestHeader("X-User-Id") Long userId,
                                     @RequestParam("id") Long id);

    @PostMapping("/category/create")
    Response<String> create(@RequestHeader("X-User-Id") Long userId,
                            @RequestParam(value = "typeName", required = false) String typeName,
                            @RequestParam(value = "typeImage", required = false) String typeImage,
                            @RequestParam("typeDesc") String typeDesc,
                            @RequestParam(value = "parentId", required = false) Long parentId);

    @PutMapping("/category/update")
    Response<String> update(@RequestHeader("X-User-Id") Long userId,
                            @RequestParam("id") Long id,
                            @RequestParam(value = "typeName", required = false) String typeName,
                            @RequestParam(value = "typeImage", required = false) String typeImage,
                            @RequestParam(value = "typeDesc", required = false) String typeDesc,
                            @RequestParam(value = "parentId", required = false) Long parentId);

    @DeleteMapping("/category/delete")
    Response<String> delete(@RequestHeader("X-User-Id") Long userId,
                            @RequestParam("id") Long id);
}


