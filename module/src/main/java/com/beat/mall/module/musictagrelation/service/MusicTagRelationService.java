package com.beat.mall.module.musictagrelation.service;

import com.beat.mall.module.musictagrelation.entity.MusicTagRelation;
import com.beat.mall.module.musictagrelation.mapper.MusicTagRelationMapper;
import com.beat.mall.module.tag.mapper.TagMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MusicTagRelationService {

    @Resource
    private MusicTagRelationMapper musicTagRelationMapper;
    @Resource
    private TagMapper tagMapper;

    public MusicTagRelation getByMusicIdAndTagId(Long musicId, Long tagId) {
        MusicTagRelation entity = musicTagRelationMapper.getById(musicId, tagId);
        if (entity == null) {
            throw new RuntimeException("MusicTagRelation不存在: " + musicId + " " + tagId);
        }
        return entity;
    }

    public MusicTagRelation extractByMusicIdAndTagId(Long musicId, Long tagId) {
        return musicTagRelationMapper.extractById(musicId, tagId);
    }

    public void update(MusicTagRelation entity) {
        int time = (int) (System.currentTimeMillis() / 1000);
        entity.setUpdateTime(time);
        musicTagRelationMapper.update(entity);
    }

    public void insert(MusicTagRelation entity) {
        int time = (int) (System.currentTimeMillis() / 1000);
        entity.setCreateTime(time);
        entity.setUpdateTime(time);
        entity.setIsDeleted(0);
        musicTagRelationMapper.insert(entity);
    }

    public int delete(Long musicId, Long tagId) {
        int time = (int) (System.currentTimeMillis() / 1000);
        return musicTagRelationMapper.delete(musicId, tagId, time);
    }

    public List<MusicTagRelation> getAll() {
        return musicTagRelationMapper.getAll();
    }

    public List<String> getTagsByMusicId(Long musicId) {
        List<Long> list = musicTagRelationMapper.getTagsByMusicId(musicId);
        List<String> tagNames = new ArrayList<>();
        for (Long i : list) {
            String tagName = tagMapper.getById(i).getTagName();
            tagNames.add(tagName);
        }
        return tagNames;
    }

    public List<Long> getMusicIdsByTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        String ids = tagIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        return musicTagRelationMapper.getMusicIdsByTagIds(ids);
    }
}