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
        try {
            tag = tagService.getById(id);
        } catch (Exception e) {
            log.error("tag不存在", e);
            return new Response<>(3052);
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
        try {
            tags = tagService.getAll();
        } catch (Exception e) {
            log.error("获取标签列表失败", e);
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
        String res = "success";
        try {
            tagService.insert(tagName, tagDesc);
        } catch (Exception e) {
            res = "fail";
            log.error("tagName cannot be null", e);
            return new Response<>(3052);
        }
        return new Response<>(1001, res);
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
        String res = "success";
        Tag tag = new Tag()
                .setId(id)
                .setTagName(tagName)
                .setTagDesc(tagDesc);
        try {
            tagService.update(tag);
        } catch (Exception e) {
            res = "fail";
            log.error("id cannot be find");
            return new Response<>(3052);
        }
        return new Response<>(1001, res);
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
        String res = "success";
        Integer affectedRows=0;
        try {
          affectedRows=  tagService.delete(id);
        } catch (Exception e) {
            res = "fail";
            log.error("删除失败", e);
            return new Response<>(3052);
        }
        if (affectedRows==0){
            log.error("删除失败");
            res="fail";
            return new Response<>(3052);
        }
        return new Response<>(1001, res);
    }
}