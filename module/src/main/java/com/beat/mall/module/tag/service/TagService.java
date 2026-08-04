package com.beat.mall.module.tag.service;

import com.beat.mall.module.musictagrelation.mapper.MusicTagRelationMapper;
import com.beat.mall.module.tag.entity.Tag;
import com.beat.mall.module.tag.mapper.TagMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TagService {

    @Resource
    private TagMapper mapper;
    @Resource
    private MusicTagRelationMapper musicTagRelationMapper;

    public Tag getById(Long id) {
        Tag entity = mapper.getById(id);
        if (entity == null) {
            throw new RuntimeException("Tag不存在: " + id);
        }
        return entity;
    }

    public Tag extractById(Long id) {
        return mapper.extractById(id);
    }

    public Tag extractByTagName(String tagName) {
        return mapper.extractByTagName(tagName);
    }

    public Long update(Tag tag) throws Exception {
        Tag tagtmp = extractById(tag.getId());
        if (tagtmp == null) {
            throw new RuntimeException("id cannot be null");
        }
        Long affectedRows = (long) mapper.update(tag);
        return affectedRows;
    }

    public Long insert(String tagName, String tagDesc) throws Exception {
        Tag tagtmp = extractByTagName(tagName);
        if (tagtmp != null) {
            throw new RuntimeException("the tag has already");
        }
        int time = (int) (System.currentTimeMillis() / 1000);
        Tag tag = new Tag()
                .setTagName(tagName)
                .setTagDesc(tagDesc)
                .setCreateTime(time)
                .setUpdateTime(time)
                .setIsDeleted(0);
        mapper.insert(tag);
        return tag.getId();
    }

    public int delete(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null");
        }
        if (musicTagRelationMapper.getMusicsByTagId(id).size() != 0) {
            throw new RuntimeException("该标签下有关联音乐，无法删除");
        }
        int time = (int) (System.currentTimeMillis() / 1000);
        return mapper.delete(id, time);
    }

    public List<Tag> getAll() {
        return mapper.getAll();
    }
}