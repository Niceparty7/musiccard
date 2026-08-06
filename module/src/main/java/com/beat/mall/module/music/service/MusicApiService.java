package com.beat.mall.module.music.service;

import com.beat.mall.module.music.entity.Music;
import com.beat.mall.module.music.mapper.MusicMapper;
import com.beat.mall.module.musictagrelation.entity.MusicTagRelation;
import com.beat.mall.module.musictagrelation.service.MusicTagRelationService;
import com.beat.mall.module.tag.entity.Tag;
import com.beat.mall.module.tag.service.TagService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MusicApiService {
    @Resource
    private MusicMapper musicMapper;
    @Resource
    private TagService tagService;
    @Resource
    private MusicTagRelationService musicTagRelationService;

    public List<Music> getAllMusic(Integer page, Integer pageSize, String keyword) {
        return musicMapper.getAllMusic((page - 1) * pageSize, pageSize, keyword);
    }

    public List<Music> getAllMusicList2(Integer page, Integer pageSize, String musicName, String typeName, String tagName) {
        return musicMapper.getAllMusicList2((page - 1) * pageSize, pageSize, musicName, typeName, tagName);
    }

    public Long countTotal(String musicName, String typeName, String tagName) {
        return musicMapper.countTotal(musicName, typeName, tagName);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate, Integer typeId, String tags)
            throws Exception {
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Music music = new Music()
                .setCoverImages(coverImages)
                .setMusicName(musicName)
                .setSingerName(singerName)
                .setMusicDesc(musicDesc)
                .setAlbumTitle(albumTitle)
                .setReleaseDate(releaseDate)
                .setCreateTime(timeStamp)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0)
                .setTypeId(typeId);
        if (coverImages == null) {
            throw new RuntimeException("coverImages cannot be null!");
        }
        if (musicName == null) {
            throw new RuntimeException("musicName cannot be null!");
        }
        if (singerName == null) {
            throw new RuntimeException("singerName cannot be null!");
        }
        musicMapper.insert(music);
        Long musicId = music.getId();
        if (tags != null && !tags.isEmpty()) {
            String[] tagstrs = tags.split("\\$");
            Long tagId = null;
            //关联标签
            for (String tag : tagstrs) {
                Tag extractTag = tagService.extractByTagName(tag);
                if (extractTag != null && extractTag.getIsDeleted() == 1) {
                    tagId = extractTag.getId();
                    Tag tmpTag = extractTag.setIsDeleted(0);
                    tagService.update(tmpTag);
                } else if (extractTag == null) {
                    tagId = tagService.insert(tag, null);
                } else {
                    tagId = extractTag.getId();
                }
                MusicTagRelation extractRelation = musicTagRelationService.extractByMusicIdAndTagId(musicId, tagId);
                if (extractRelation != null && extractRelation.getIsDeleted() == 1) {
                    MusicTagRelation tmpRelation = extractRelation.
                            setUpdateTime(timeStamp).
                            setIsDeleted(0);
                    musicTagRelationService.update(tmpRelation);
                }
                if (extractRelation == null) {
                    MusicTagRelation musicTagRelation = new MusicTagRelation()
                            .setMusicId(musicId)
                            .setTagId(tagId)
                            .setCreateTime(timeStamp)
                            .setUpdateTime(timeStamp)
                            .setIsDeleted(0);
                    musicTagRelationService.insert(musicTagRelation);
                }
            }
        }
        return musicId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long update(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate, Integer typeId, String tags)
            throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Music music = new Music()
                .setId(id)
                .setCoverImages(coverImages)
                .setMusicName(musicName)
                .setSingerName(singerName)
                .setMusicDesc(musicDesc)
                .setAlbumTitle(albumTitle)
                .setReleaseDate(releaseDate)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0)
                .setTypeId(typeId);
        if (musicMapper.extractById(id) == null) {
            throw new RuntimeException("cannot find the id");
        }
        Long affectedRows = (long) musicMapper.update(music);
        if (tags != null && !tags.isEmpty()) {
            String[] tagstrs = tags.split("\\$");
            Long tagId = null;
            //关联标签
            for (String tag : tagstrs) {
                Tag extractTag = tagService.extractByTagName(tag);
                if (extractTag != null && extractTag.getIsDeleted() == 1) {
                    tagId = extractTag.getId();
                    Tag tmpTag = extractTag.setIsDeleted(0);
                    tagService.update(tmpTag);
                } else if (extractTag == null) {
                    tagId = tagService.insert(tag, null);
                } else {
                    tagId = extractTag.getId();
                }
                MusicTagRelation extractRelation = musicTagRelationService.extractByMusicIdAndTagId(id, tagId);
                if (extractRelation != null && extractRelation.getIsDeleted() == 1) {
                    MusicTagRelation tmpRelation = extractRelation.
                            setUpdateTime(timeStamp).
                            setIsDeleted(0);
                    musicTagRelationService.update(tmpRelation);
                }
                if (extractRelation == null) {
                    MusicTagRelation musicTagRelation = new MusicTagRelation()
                            .setMusicId(id)
                            .setTagId(tagId)
                            .setCreateTime(timeStamp)
                            .setUpdateTime(timeStamp)
                            .setIsDeleted(0);
                    musicTagRelationService.insert(musicTagRelation);
                }
            }
        }
        return affectedRows;
    }
}