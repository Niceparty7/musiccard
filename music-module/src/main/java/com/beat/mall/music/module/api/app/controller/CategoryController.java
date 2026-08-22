package com.beat.mall.music.module.api.app.controller;

import com.beat.mall.common.api.app.category.CategoryChildrenListVO;
import com.beat.mall.common.api.app.category.CategoryListFeedVO;
import com.beat.mall.common.api.app.category.CategoryListVO;
import com.beat.mall.common.entity.category.Category;
import com.beat.mall.common.response.Response;
import com.beat.mall.music.module.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController("appCategoryController")
@RequiredArgsConstructor
@RequestMapping(headers = {"X-Client-Type=app", "X-Internal-Token"})
public class CategoryController {
    private final CategoryService categoryService;

    @RequestMapping("/category/list")
    public Response<CategoryListFeedVO> getCategoryList() {
        List<CategoryListVO> list = new ArrayList<>();
        for (Category category : categoryService.getAllCategory()) {
            List<CategoryChildrenListVO> children = new ArrayList<>();
            for (Long childId : categoryService.getChildrenById(category.getId())) {
                try {
                    Category child = categoryService.getById(childId);
                    if (child != null) {
                        children.add(new CategoryChildrenListVO()
                                .setTypeName(child.getTypeName())
                                .setTypeImage(child.getTypeImage()));
                    }
                } catch (Exception exception) {
                    log.warn("Cannot load child category: {}", childId, exception);
                }
            }
            list.add(new CategoryListVO()
                    .setTypeName(category.getTypeName())
                    .setTypeImage(category.getTypeImage())
                    .setChildren(children));
        }
        return new Response<>(1001, new CategoryListFeedVO().setList(list));
    }
}



