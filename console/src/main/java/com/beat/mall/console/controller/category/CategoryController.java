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
        Long id = null;
        String res = "";
        typeName = typeName == null ? typeName : typeName.trim();
        typeImage = typeImage == null ? typeImage : typeImage.trim();
        try {
            id = categoryService.edit(null, typeName, typeImage, typeDesc, parentId);
        } catch (Exception e) {
            res = "typeName typeImage 等字段不能为空";
            log.error("typeName and typeImage cannot be null", e);
        }
        if (id != null) {
            res = "成功";
        } else {
            res = "失败 " + res;
        }
        return new Response<>(1001, res);
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
        String res = "成功";
        typeName = typeName == null ? typeName : typeName.trim();
        typeImage = typeImage == null ? typeImage : typeImage.trim();
        try {
            categoryService.edit(id, typeName, typeImage, typeDesc, parentId);
        } catch (Exception e) {
            res = "typeName typeImage 等字段不能为空";
            log.error("typeName and typeImage cannot be null", e);
        }
        return new Response<>(1001, res);
    }

    @RequestMapping("/category/delete")
    public Response categoryDelete(@VerifiedUser User loginUser, @RequestParam(value = "id") Long id) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        Long countMusicNum = musicService.getByTypeId(id);
        if (countMusicNum > 0) {
            log.error("删除失败，该分类下仍有音乐，无法删除");
            return new Response<>(1001, "删除失败，该分类下仍有音乐，无法删除");
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
        return new Response<>(1001, res);
    }
}