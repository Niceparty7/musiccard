package com.beat.mall.app.feign;

import com.beat.mall.common.api.app.category.CategoryListFeedVO;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "module", contextId = "categoryFeign")
public interface CategoryFeign {
    @GetMapping("/category/list")
    Response<CategoryListFeedVO> getCategoryList();
}
