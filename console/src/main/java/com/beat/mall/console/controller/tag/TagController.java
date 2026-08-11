package com.beat.mall.console.controller.tag;

import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.domain.tag.TagInfoVO;
import com.beat.mall.console.domain.tag.TagListFeedVO;
import com.beat.mall.console.domain.tag.TagListVO;
import com.beat.mall.module.tag.entity.Tag;
import com.beat.mall.module.tag.service.TagService;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tag")
@Slf4j
public class TagController {

    @Resource
    private TagService tagService;

    /**
     * 详情接口
     */
    @RequestMapping("/info")
    public Response<TagInfoVO> getDetail(@VerifiedUser User loginUser, @RequestParam(name = "id") Long id) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response<>(1002);
        }
        Tag tag = null;
        boolean success = true;
        try {
            tag = tagService.getById(id);
        } catch (Exception e) {
            success = false;
            log.error("tag不存在, id:{}", id, e);
        }
        if (!success || tag == null) {
            return new Response<>(4008);
        }
        TagInfoVO tagInfoVO = new TagInfoVO()
                .setTagName(tag.getTagName())
                .setTagDesc(tag.getTagDesc());
        return new Response<>(1001, tagInfoVO);
    }

    /**
     * 列表接口
     */
    @RequestMapping("/list")
    public Response<TagListFeedVO> getAll(@VerifiedUser User loginUser) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response<>(1002);
        }
        List<TagListVO> tagListVOS = new ArrayList<>();
        List<Tag> tags = null;
        boolean success = true;
        try {
            tags = tagService.getAll();
        } catch (Exception e) {
            success = false;
            log.error("获取标签列表失败", e);
        }
        if (!success || tags == null) {
            return new Response<>(4005);
        }
        for (Tag tag : tags) {
            TagListVO tagListVO = new TagListVO().setTag(tag.getTagName());
            tagListVOS.add(tagListVO);
        }
        TagListFeedVO tagListFeedVO = new TagListFeedVO().setList(tagListVOS);
        return new Response<>(1001, tagListFeedVO);
    }

    /**
     * 新增接口
     */
    @PostMapping("/create")
    public Response<String> create(@VerifiedUser User loginUser, @RequestParam(name = "tagName") String tagName, @RequestParam(name = "tagDesc", required = false) String tagDesc) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response<>(1002);
        }
        tagName = tagName == null ? tagName : tagName.trim();
        boolean success = true;
        try {
            tagService.insert(tagName, tagDesc);
        } catch (Exception e) {
            success = false;
            log.error("tagName cannot be null, tagName:{}", tagName, e);
        }
        if (!success) {
            return new Response<>(4005, "创建失败：tagName不能为空或标签已存在");
        }
        return new Response<>(1001, "success");
    }

    /**
     * 更新接口
     */
    @PutMapping("/update")
    public Response<String> update(@VerifiedUser User loginUser,
                                   @RequestParam("id") Long id,
                                   @RequestParam(name = "tagName") String tagName,
                                   @RequestParam(name = "tagDesc", required = false) String tagDesc) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response<>(1002);
        }
        tagName = tagName == null ? tagName : tagName.trim();
        Tag tag = new Tag()
                .setId(id)
                .setTagName(tagName)
                .setTagDesc(tagDesc);
        boolean success = true;
        try {
            tagService.update(tag);
        } catch (Exception e) {
            success = false;
            log.error("update tag fail, id:{}", id, e);
        }
        if (!success) {
            return new Response<>(4005, "更新失败：id不存在");
        }
        return new Response<>(1001, "success");
    }

    /**
     * 删除接口
     */
    @DeleteMapping("/delete")
    public Response<String> delete(@VerifiedUser User loginUser, @RequestParam("id") Long id) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response<>(1002);
        }
        boolean success = true;
        Integer affectedRows = 0;
        try {
            affectedRows = tagService.delete(id);
        } catch (Exception e) {
            success = false;
            log.error("删除失败, id:{}", id, e);
        }
        if (!success || affectedRows == 0) {
            return new Response<>(4005, "删除失败：id不存在");
        }
        return new Response<>(1001, "success");
    }
}