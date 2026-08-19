package com.beat.mall.module.api.console.controller;

import com.beat.mall.common.api.console.tag.TagInfoVO;
import com.beat.mall.common.api.console.tag.TagListFeedVO;
import com.beat.mall.common.api.console.tag.TagListVO;
import com.beat.mall.common.entity.tag.Tag;
import com.beat.mall.common.response.Response;
import com.beat.mall.module.auth.ProviderAuthService;
import com.beat.mall.module.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tag")
public class ConsoleTagProviderController {
    private final TagService tagService;
    private final ProviderAuthService providerAuthService;

    @RequestMapping(value = "/info", headers = "X-Client-Type=console")
    public Response<TagInfoVO> getDetail(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long id) {
        providerAuthService.requireUser(userId);
        Tag tag = tagService.getById(id);
        if (tag == null) {
            return new Response<>(4008);
        }
        return new Response<>(1001, new TagInfoVO()
                .setTagName(tag.getTagName())
                .setTagDesc(tag.getTagDesc()));
    }

    @RequestMapping(value = "/list", headers = "X-Client-Type=console")
    public Response<TagListFeedVO> getAll(@RequestHeader("X-User-Id") Long userId) {
        providerAuthService.requireUser(userId);
        List<TagListVO> result = new ArrayList<>();
        for (Tag tag : tagService.getAll()) {
            result.add(new TagListVO().setTag(tag.getTagName()));
        }
        return new Response<>(1001, new TagListFeedVO().setList(result));
    }

    @PostMapping(value = "/create", headers = "X-Client-Type=console")
    public Response<String> create(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String tagName,
            @RequestParam(required = false) String tagDesc) throws Exception {
        providerAuthService.requireUser(userId);
        tagService.insert(tagName == null ? null : tagName.trim(), tagDesc);
        return new Response<>(1001, "success");
    }

    @PutMapping(value = "/update", headers = "X-Client-Type=console")
    public Response<String> update(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long id,
            @RequestParam String tagName,
            @RequestParam(required = false) String tagDesc) throws Exception {
        providerAuthService.requireUser(userId);
        tagService.update(new Tag().setId(id)
                .setTagName(tagName == null ? null : tagName.trim())
                .setTagDesc(tagDesc));
        return new Response<>(1001, "success");
    }

    @DeleteMapping(value = "/delete", headers = "X-Client-Type=console")
    public Response<String> delete(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long id) {
        providerAuthService.requireUser(userId);
        Integer affectedRows = tagService.delete(id);
        return affectedRows == null || affectedRows == 0
                ? new Response<>(4005, "删除失败：id不存在")
                : new Response<>(1001, "success");
    }
}
