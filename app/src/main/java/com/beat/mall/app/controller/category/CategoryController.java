package com.beat.mall.app.controller.category;

import com.beat.mall.app.feign.AppCategoryFeign;
import com.beat.mall.common.api.app.category.CategoryListFeedVO;
import com.beat.mall.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final AppCategoryFeign categoryFeign;

    @GetMapping("/category/list")
    public Response<CategoryListFeedVO> getCategoryList() {
        return categoryFeign.getCategoryList();
    }
}
