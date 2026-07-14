package top.yuhanpeng.musiccard.console.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.yuhanpeng.musiccard.console.domain.CategoryInfoVO;
import top.yuhanpeng.musiccard.console.domain.CategoryListFeedVO;
import top.yuhanpeng.musiccard.console.domain.CategoryListVO;
import top.yuhanpeng.musiccard.module.entity.Category;
import top.yuhanpeng.musiccard.module.service.CategoryService;
import top.yuhanpeng.musiccard.module.service.MusicService;

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
    @Autowired
    private MusicService musicService;

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

    @RequestMapping("/category/info")
    public CategoryInfoVO getCategoryInfoById(@RequestParam(value = "id") Long id) {
        Category category = null;
        try {
            category = categoryService.getById(id);
        } catch (Exception e) {
            log.error("category cannot be null", e);
        }
        if (category==null){
            log.info("id不存在");
            return null;
        }
        CategoryInfoVO categoryInfoVO = new CategoryInfoVO();
        categoryInfoVO.setTypeName(category.getTypeName())
                .setTypeImage(category.getTypeImage())
                .setTypeDesc(category.getTypeDesc());
        return categoryInfoVO;
    }

    @RequestMapping("/category/create")
    public String categoryCreate(@RequestParam(value = "typeName", required = false) String typeName,
                                 @RequestParam(value = "typeImage", required = false) String typeImage,
                                 @RequestParam(value = "typeDesc", defaultValue = "暂无描述") String typeDesc) {
        Long id = null;
        String res = "";
        typeName = typeName == null ? typeName : typeName.trim();
        typeImage = typeImage == null ? typeImage : typeImage.trim();
        try {
            id = categoryService.edit(null, typeName, typeImage, typeDesc);
        } catch (Exception e) {
            res = "typeName typeImage 等字段不能为空";
            log.error("typeName and typeImage cannot be null", e);
        }
        if (id != null) {
            res = "成功";
        } else {
            res = "失败 " + res;
        }
        return res;
    }

    @RequestMapping("/category/update")
    public String categoryUpdate(@RequestParam(value = "id") Long id,
                                 @RequestParam(value = "typeName", required = false) String typeName,
                                 @RequestParam(value = "typeImage", required = false) String typeImage,
                                 @RequestParam(value = "typeDesc", required = false) String typeDesc) {
        String res = "成功";
        typeName = typeName == null ? typeName : typeName.trim();
        typeImage = typeImage == null ? typeImage : typeImage.trim();
        try {
            categoryService.edit(id, typeName, typeImage, typeDesc);
        } catch (Exception e) {
            res = "typeName typeImage 等字段不能为空";
            log.error("typeName and typeImage cannot be null", e);
        }
        return res;
    }

    @RequestMapping("/category/delete")
    public String categoryDelete(@RequestParam(value = "id") Long id) {
        Long countMusicNum = musicService.getByTypeId(id);
        if (countMusicNum > 0) {
            log.error("删除失败，该分类下仍有音乐，无法删除");
            return "删除失败，该分类下仍有音乐，无法删除";
        }
        Integer affectedRows = 0;
        String res = "";
        try {
            affectedRows = categoryService.delete(id);
        } catch (Exception e) {
            res = "无法找到该id";
            log.error("cannot find the id", e);
        }
        if (affectedRows != 0) {
            res = "成功";
        } else {
            res = "失败 " + res;
        }
        return res;
    }
}