package com.beat.mall.music.app.feign;

import com.beat.mall.music.app.config.AppFeignConfiguration;

import com.beat.mall.common.api.app.category.CategoryListFeedVO;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "music-module", contextId = "appCategoryFeign", configuration = AppFeignConfiguration.class)
public interface CategoryFeign {
    @GetMapping("/category/list")
    Response<CategoryListFeedVO> getCategoryList();
}


