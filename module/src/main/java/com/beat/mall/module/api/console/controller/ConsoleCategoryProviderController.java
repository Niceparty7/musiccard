package com.beat.mall.module.api.console.controller;

import com.beat.mall.common.api.console.category.CategoryChildrenListVO;
import com.beat.mall.common.api.console.category.CategoryInfoVO;
import com.beat.mall.common.api.console.category.CategoryListFeedVO;
import com.beat.mall.common.api.console.category.CategoryListVO;
import com.beat.mall.common.entity.category.Category;
import com.beat.mall.common.response.Response;
import com.beat.mall.module.auth.ProviderAuthService;
import com.beat.mall.module.category.service.CategoryService;
import com.beat.mall.module.music.service.MusicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class ConsoleCategoryProviderController {
    private final CategoryService categoryService;
    private final MusicService musicService;
    private final ProviderAuthService providerAuthService;

    @RequestMapping(value = "/list", headers = "X-Client-Type=console")
    public Response<CategoryListFeedVO> getCategoryList(@RequestHeader("X-User-Id") Long userId) {
        providerAuthService.requireUser(userId);
        return new Response<>(1001, new CategoryListFeedVO().setList(buildList()));
    }

    @RequestMapping(value = "/info", headers = "X-Client-Type=console")
    public Response<CategoryInfoVO> getCategoryInfo(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam("id") Long id) throws Exception {
        providerAuthService.requireUser(userId);
        Category category = categoryService.getById(id);
        if (category == null) {
            return new Response<>(3052, null);
        }
        List<CategoryChildrenListVO> children = buildChildren(id);
        CategoryInfoVO result = new CategoryInfoVO()
                .setTypeName(category.getTypeName())
                .setTypeImage(category.getTypeImage())
                .setTypeDesc(category.getTypeDesc())
                .setChildren(children);
        return new Response<>(1001, result);
    }

    @RequestMapping(value = "/create", headers = "X-Client-Type=console")
    public Response<String> create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false) String typeImage,
            @RequestParam(defaultValue = "暂无描述") String typeDesc,
            @RequestParam(required = false) Long parentId) throws Exception {
        providerAuthService.requireUser(userId);
        Long id = categoryService.edit(null, trim(typeName), trim(typeImage), typeDesc, parentId);
        return id == null
                ? new Response<>(4005, "创建失败：typeName、typeImage不能为空")
                : new Response<>(1001, "成功");
    }

    @RequestMapping(value = "/update", headers = "X-Client-Type=console")
    public Response<String> update(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long id,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false) String typeImage,
            @RequestParam(required = false) String typeDesc,
            @RequestParam(required = false) Long parentId) throws Exception {
        providerAuthService.requireUser(userId);
        categoryService.edit(id, trim(typeName), trim(typeImage), typeDesc, parentId);
        return new Response<>(1001, "成功");
    }

    @RequestMapping(value = "/delete", headers = "X-Client-Type=console")
    public Response<String> delete(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long id) throws Exception {
        providerAuthService.requireUser(userId);
        Long musicCount = musicService.getByTypeId(id);
        if (musicCount != null && musicCount > 0) {
            return new Response<>(4005, "删除失败，该分类下仍有音乐，无法删除");
        }
        Integer affectedRows = categoryService.delete(id);
        return affectedRows == null || affectedRows == 0
                ? new Response<>(4005, "删除失败：id不存在")
                : new Response<>(1001, "成功");
    }

    private List<CategoryListVO> buildList() {
        List<CategoryListVO> list = new ArrayList<>();
        for (Category category : categoryService.getAllCategory()) {
            list.add(new CategoryListVO()
                    .setTypeName(category.getTypeName())
                    .setTypeImage(category.getTypeImage())
                    .setChildren(buildChildren(category.getId())));
        }
        return list;
    }

    private List<CategoryChildrenListVO> buildChildren(Long categoryId) {
        List<CategoryChildrenListVO> children = new ArrayList<>();
        for (Long childId : categoryService.getChildrenById(categoryId)) {
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
        return children;
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
