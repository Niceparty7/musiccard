package com.beat.mall.music.app.controller.category;

import com.beat.mall.music.app.feign.CategoryFeign;
import com.beat.mall.common.api.app.category.CategoryListFeedVO;
import com.beat.mall.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController("appCategoryController")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryFeign categoryFeign;

    @GetMapping("/category/list")
    public Response<CategoryListFeedVO> getCategoryList() {
        return categoryFeign.getCategoryList();
    }
}


