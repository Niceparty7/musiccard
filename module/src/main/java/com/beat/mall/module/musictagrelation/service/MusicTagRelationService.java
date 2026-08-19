package com.beat.mall.module.musictagrelation.service;

import com.beat.mall.module.annotation.ReadOnly;
import com.beat.mall.common.entity.musictagrelation.MusicTagRelation;
import com.beat.mall.module.musictagrelation.mapper.MusicTagRelationMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MusicTagRelationService {

    @Resource
    private MusicTagRelationMapper musicTagRelationMapper;

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

    @ReadOnly
    public List<MusicTagRelation> getAll() {
        return musicTagRelationMapper.getAll();
    }

    @ReadOnly
    public List<Long> getMusicIdsByTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        String ids = tagIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        return musicTagRelationMapper.getMusicIdsByTagIds(ids);
    }

    @ReadOnly
    public List<Long> getTagsByMusicId(Long musicId) {
        return musicTagRelationMapper.getTagsByMusicId(musicId);
    }

    public List<Long> getMusicsByTagId(Long tagId) {
        return musicTagRelationMapper.getMusicsByTagId(tagId);
    }
}