package com.beat.mall.console.controller.category;

import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.domain.category.CategoryChildrenListVO;
import com.beat.mall.console.domain.category.CategoryInfoVO;
import com.beat.mall.console.domain.category.CategoryListFeedVO;
import com.beat.mall.console.domain.category.CategoryListVO;
import com.beat.mall.module.category.entity.Category;
import com.beat.mall.module.category.service.CategoryService;
import com.beat.mall.module.music.service.MusicService;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @Autowired
    private MusicService musicService;

    @RequestMapping("/category/list")
    public Response getCategoryList(@VerifiedUser User loginUser) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
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
        return new Response<>(1001, categoryListFeedVO);
    }

    @RequestMapping("/category/info")
    public Response getCategoryInfoById(@VerifiedUser User loginUser, @RequestParam(value = "id") Long id) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        Category category = null;
        try {
            category = categoryService.getById(id);
        } catch (Exception e) {
            log.error("category cannot be null", e);
        }
        if (category == null) {
            log.info("id不存在");
            return new Response<>(3052, "id不存在");
        }
        List<Long> childrenIds = categoryService.getChildrenById(id);
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
        CategoryInfoVO categoryInfoVO = new CategoryInfoVO()
                .setTypeName(category.getTypeName())
                .setTypeImage(category.getTypeImage())
                .setTypeDesc(category.getTypeDesc())
                .setChildren(categoryChildrenListVOS);
        return new Response<>(1001, categoryInfoVO);
    }

    @RequestMapping("/category/create")
    public Response categoryCreate(@VerifiedUser User loginUser,
                                   @RequestParam(value = "typeName", required = false) String typeName,
                                   @RequestParam(value = "typeImage", required = false) String typeImage,
                                   @RequestParam(value = "typeDesc", defaultValue = "暂无描述") String typeDesc,
                                   @RequestParam(value = "parentId", required = false) Long parentId) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        boolean success = true;
        Long id = null;
        typeName = typeName == null ? typeName : typeName.trim();
        typeImage = typeImage == null ? typeImage : typeImage.trim();
        try {
            id = categoryService.edit(null, typeName, typeImage, typeDesc, parentId);
        } catch (Exception e) {
            success = false;
            log.error("create category fail, typeName:{}", typeName, e);
        }
        if (!success || id == null) {
            return new Response<>(4005, "创建失败：typeName、typeImage不能为空");
        }
        return new Response<>(1001, "成功");
    }

    @RequestMapping("/category/update")
    public Response categoryUpdate(@VerifiedUser User loginUser,
                                   @RequestParam(value = "id") Long id,
                                   @RequestParam(value = "typeName", required = false) String typeName,
                                   @RequestParam(value = "typeImage", required = false) String typeImage,
                                   @RequestParam(value = "typeDesc", required = false) String typeDesc,
                                   @RequestParam(value = "parentId", required = false) Long parentId) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        boolean success = true;
        typeName = typeName == null ? typeName : typeName.trim();
        typeImage = typeImage == null ? typeImage : typeImage.trim();
        try {
            categoryService.edit(id, typeName, typeImage, typeDesc, parentId);
        } catch (Exception e) {
            success = false;
            log.error("update category fail, id:{}", id, e);
        }
        if (!success) {
            return new Response<>(4005, "更新失败：typeName、typeImage不能为空或id不存在");
        }
        return new Response<>(1001, "成功");
    }

    @RequestMapping("/category/delete")
    public Response categoryDelete(@VerifiedUser User loginUser, @RequestParam(value = "id") Long id) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        Long countMusicNum = musicService.getByTypeId(id);
        if (countMusicNum > 0) {
            log.warn("删除失败，该分类下仍有音乐，分类id:{}", id);
            return new Response<>(4005, "删除失败，该分类下仍有音乐，无法删除");
        }
        boolean success = true;
        Integer affectedRows = 0;
        try {
            affectedRows = categoryService.delete(id);
        } catch (Exception e) {
            success = false;
            log.error("delete category fail, id:{}", id, e);
        }
        if (!success || affectedRows == 0) {
            return new Response<>(4005, "删除失败：id不存在");
        }
        return new Response<>(1001, "成功");
    }
}