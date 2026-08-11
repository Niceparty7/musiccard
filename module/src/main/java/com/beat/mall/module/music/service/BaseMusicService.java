package com.beat.mall.module.music.service;

import com.beat.mall.module.category.entity.Category;
import com.beat.mall.module.category.service.CategoryService;
import com.beat.mall.module.music.entity.Music;
import com.beat.mall.module.musictagrelation.entity.MusicTagRelation;
import com.beat.mall.module.musictagrelation.service.MusicTagRelationService;
import com.beat.mall.module.tag.entity.Tag;
import com.beat.mall.module.tag.service.TagService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BaseMusicService {
    @Autowired
    private MusicService musicService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private TagService tagService;
    @Resource
    private MusicTagRelationService musicTagRelationService;

    public List<Music> getAllMusic(Integer page, Integer pageSize, String keyword) {
        String subquery = "";
        String subquery2 = "";
        if (keyword != null && !keyword.isEmpty()) {
            List<Category> categoryList = categoryService.getCategoryByKeyword(keyword);
            List<Long> ids = new ArrayList<>();
            List<Long> parentIds = new ArrayList<>();
            Set<Long> idSet = new HashSet<>();
            for (Category category : categoryList) {
                ids.add(category.getId());
                parentIds.add(category.getParentId());
                idSet.addAll(categoryService.getChildrenById(category.getId()));
            }
            idSet.addAll(ids);
            idSet.addAll(parentIds);
            List<Long> allIds = new ArrayList<>(idSet);
            subquery = getSubqueryByIds(allIds);
            List<Long> tagIds = tagService.getTagIdsByKeyword(keyword);
            List<Long> allMusicIds = musicTagRelationService.getMusicIdsByTagIds(tagIds);
            subquery2 = getSubqueryByIds(allMusicIds);
        }
        return musicService.getAllMusic((page - 1) * pageSize, pageSize, keyword, subquery, subquery2);
    }

    private String getSubqueryByIds(List<Long> list) {
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                stringBuffer.append(list.get(i) + "");
                break;
            }
            stringBuffer.append(list.get(i) + "").append(",");
        }
        return stringBuffer.toString();
    }

    public List<Music> getAllMusicList2(Integer page, Integer pageSize, String musicName, String typeName, String tagName) {
        String subquery = "";
        String subquery2 = "";
        if (typeName != null && !typeName.isEmpty()) {
            List<Category> categoryList = categoryService.getCategoryByKeyword(typeName);
            List<Long> ids = new ArrayList<>();
            List<Long> parentIds = new ArrayList<>();
            Set<Long> idSet = new HashSet<>();
            for (Category category : categoryList) {
                ids.add(category.getId());
                parentIds.add(category.getParentId());
                idSet.addAll(categoryService.getChildrenById(category.getId()));
            }
            idSet.addAll(ids);
            idSet.addAll(parentIds);
            List<Long> allIds = new ArrayList<>(idSet);
            subquery = getSubqueryByIds(allIds);
        }
        if (tagName != null && !tagName.isEmpty()) {
            List<Long> tagIds = tagService.getTagIdsByKeyword(tagName);
            List<Long> allMusicIds = musicTagRelationService.getMusicIdsByTagIds(tagIds);
            subquery2 = getSubqueryByIds(allMusicIds);
        }
        return musicService.getAllMusicList2((page - 1) * pageSize, pageSize, musicName, subquery, subquery2);
    }

    public Long countTotal(String musicName, String typeName, String tagName) {
        String subquery = "";
        String subquery2 = "";
        if (typeName != null && !typeName.isEmpty()) {
            List<Category> categoryList = categoryService.getCategoryByKeyword(typeName);
            List<Long> ids = new ArrayList<>();
            List<Long> parentIds = new ArrayList<>();
            Set<Long> idSet = new HashSet<>();
            for (Category category : categoryList) {
                ids.add(category.getId());
                parentIds.add(category.getParentId());
                idSet.addAll(categoryService.getChildrenById(category.getId()));
            }
            idSet.addAll(ids);
            idSet.addAll(parentIds);
            List<Long> allIds = new ArrayList<>(idSet);
            subquery = getSubqueryByIds(allIds);
        }
        if (tagName != null && !tagName.isEmpty()) {
            List<Long> tagIds = tagService.getTagIdsByKeyword(tagName);
            List<Long> allMusicIds = musicTagRelationService.getMusicIdsByTagIds(tagIds);
            subquery2 = getSubqueryByIds(allMusicIds);
        }
        return musicService.countTotal(musicName, subquery, subquery2);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long edit(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate, Integer typeId, String tags)
            throws Exception {
        Long res;
        if (typeId != null) {
            Category category = categoryService.getById((long) typeId);
            if (category == null) {
                throw new RuntimeException("cannot find the typeId");
            }
        }
        if (id != null) {
            res = update(id, coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate, typeId, tags);
            if (res == 0) {
                throw new RuntimeException("update fail!");
            }
        } else {
            res = create(coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate, typeId, tags);
            if (res == null) {
                throw new RuntimeException("create fail!");
            }
        }
        return res;
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
        musicService.insert(music);
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
        if (musicService.extractById(id) == null) {
            throw new RuntimeException("cannot find the id");
        }
        Long affectedRows = (long) musicService.update(music);
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

    public List<String> getTagsByMusicId(Long musicId) {
        List<Long> list = musicTagRelationService.getTagsByMusicId(musicId);
        List<String> tagNames = new ArrayList<>();
        for (Long i : list) {
            String tagName = tagService.getById(i).getTagName();
            tagNames.add(tagName);
        }
        return tagNames;
    }

    public int delete(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null");
        }
        if (musicTagRelationService.getMusicsByTagId(id).size() != 0) {
            throw new RuntimeException("该标签下有关联音乐，无法删除");
        }
        return tagService.delete(id);
    }
}