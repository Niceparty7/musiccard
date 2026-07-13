package top.yuhanpeng.musiccard.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yuhanpeng.musiccard.app.domain.CategoryListFeedVO;
import top.yuhanpeng.musiccard.app.domain.CategoryListVO;
import top.yuhanpeng.musiccard.module.entity.Category;
import top.yuhanpeng.musiccard.module.service.CategoryService;

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

    public CategoryListFeedVO getCategoryList() {
        List<Category> categories = categoryService.getAllCategory();
        List<CategoryListVO> list = new ArrayList<>();
        for (Category category : categories) {
            CategoryListVO categoryListVO = new CategoryListVO();
            categoryListVO.setTypeName(category.getTypeName())
                    .setTypeImage(category.getTypeImage());
            list.add(categoryListVO);
        }
        CategoryListFeedVO categoryListFeedVO = new CategoryListFeedVO();
        categoryListFeedVO.setList(list);
        return categoryListFeedVO;
    }
}