package com.beat.mall.app.controller.category;

import com.beat.mall.app.domain.category.CategoryListFeedVO;
import com.beat.mall.app.domain.category.CategoryListVO;
import com.beat.mall.module.category.entity.Category;
import com.beat.mall.module.category.service.CategoryService;
import com.beat.mall.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 音乐类型表 前端控制器
 * </p>
 *
 * @author YHP
 * @since 2026-07-12
 */
@Slf4j
@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/category/list")

    public Response getCategoryList() {
        List<Category> categories = categoryService.getAllCategory();
        List<CategoryListVO> list = new ArrayList<>();
        for (Category category : categories) {
            CategoryListVO categoryListVO = new CategoryListVO()
                    .setTypeName(category.getTypeName())
                    .setTypeImage(category.getTypeImage());
            list.add(categoryListVO);
        }
        CategoryListFeedVO categoryListFeedVO = new CategoryListFeedVO()
                .setList(list);
        return new Response(1001, categoryListFeedVO);
    }
}