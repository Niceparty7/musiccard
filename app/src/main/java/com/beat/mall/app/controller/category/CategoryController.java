package com.beat.mall.app.controller.category;

import com.beat.mall.app.domain.category.CategoryChildrenListVO;
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
            List<Long> childrenIds = categoryService.getChildrenById(category.getId());
            List<CategoryChildrenListVO> categoryChildrenListVOS = new ArrayList<>();
            for (Long childId : childrenIds) {
                Category child = null;
                try {
                    child = categoryService.getById(childId);
                } catch (Exception e) {
                    log.error("cannot find the current childId:{}", child, e);
                }
                //当前child未找到跳过
                if (child == null) {
                    continue;
                }
                categoryChildrenListVOS.add(new CategoryChildrenListVO()
                        .setTypeName(child.getTypeName())
                        .setTypeImage(child.getTypeImage()));
            }
            CategoryListVO categoryListVO = new CategoryListVO()
                    .setTypeName(category.getTypeName())
                    .setTypeImage(category.getTypeImage())
                    .setChildren(categoryChildrenListVOS);
            list.add(categoryListVO);
        }
        CategoryListFeedVO categoryListFeedVO = new CategoryListFeedVO()
                .setList(list);
        return new Response(1001, categoryListFeedVO);
    }
}