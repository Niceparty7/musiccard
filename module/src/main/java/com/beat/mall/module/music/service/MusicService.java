package com.beat.mall.module.music.service;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.EasyExcel;
import com.beat.mall.module.category.entity.Category;
import com.beat.mall.module.category.service.CategoryService;
import com.beat.mall.module.music.domain.MusicExcelDTO;
import com.beat.mall.module.music.domain.MusicListDTO;
import com.beat.mall.module.music.entity.Music;
import com.beat.mall.module.music.listener.MusicExcelListener;
import com.beat.mall.module.music.mapper.MusicMapper;
import com.beat.mall.module.musictagrelation.entity.MusicTagRelation;
import com.beat.mall.module.musictagrelation.service.MusicTagRelationService;
import com.beat.mall.module.tag.entity.Tag;
import com.beat.mall.module.tag.service.TagService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class MusicService {
    private final Executor excelExecutor;
    @Resource
    private MusicMapper musicMapper;
    @Resource
    private CategoryService categoryService;
    @Resource
    private TagService tagService;
    @Resource
    private MusicTagRelationService musicTagRelationService;

    public Music getById(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        Music music = musicMapper.getById(id);
        if (music == null) {
            throw new RuntimeException("music is null!");
        }
        return music;
    }

    public Music extractById(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        Music music = musicMapper.extractById(id);
        if (music == null) {
            throw new RuntimeException("music is null!");
        }
        return music;
    }

    public List<Music> getAllMusic(Integer page, Integer pageSize, String keyword) {
        List<Long> ids = musicMapper.getIds(keyword);
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < ids.size(); i++) {
            if (i == ids.size() - 1) {
                stringBuffer.append(ids.get(i) + "");
                break;
            }
            stringBuffer.append(ids.get(i) + "").append(",");
        }
        String subquery = stringBuffer.toString();
        return musicMapper.getAllMusic((page - 1) * pageSize, pageSize, keyword, subquery);
    }

    public List<MusicListDTO> getAllMusicListDTO(Integer page, Integer pageSize, String keyword) {
        return musicMapper.getAllMusicListDTO((page - 1) * pageSize, pageSize, keyword);
    }

    public Long countTotal(String keyword) {
        return musicMapper.countTotal(keyword);
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
        if (extractById(id) == null) {
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
    public Integer delete(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        return musicMapper.delete(timeStamp, id);
    }

    public Long getByTypeId(Long typeId) {
        return musicMapper.getByTypeId(typeId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void export(OutputStream outputStream) throws Exception {
        List<Music> list = musicMapper.getAllMusicList();
        List<MusicExcelDTO> excelList = list.stream()
                .map(item -> {
                    MusicExcelDTO musicExcelDTO = new MusicExcelDTO();
                    musicExcelDTO.setMusicName(item.getMusicName());
                    musicExcelDTO.setSingerName(item.getSingerName());
                    musicExcelDTO.setCoverImages(item.getCoverImages());
                    musicExcelDTO.setMusicDesc(item.getMusicDesc());
                    musicExcelDTO.setAlbumTitle(item.getAlbumTitle());
                    musicExcelDTO.setReleaseDate(item.getReleaseDate());
                    musicExcelDTO.setIsDeleted(item.getIsDeleted());
                    musicExcelDTO.setTypeId(item.getTypeId());
                    musicExcelDTO.setCreateTime(item.getCreateTime());
                    musicExcelDTO.setUpdateTime(item.getUpdateTime());
                    return musicExcelDTO;
                }).toList();
        EasyExcel.write(outputStream, MusicExcelDTO.class)
                .sheet("音乐数据")
                .doWrite(excelList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void upload(InputStream inputStream) throws Exception {
        EasyExcel.read(inputStream, MusicExcelDTO.class, new MusicExcelListener(musicMapper)).sheet().doRead();
    }

    public File exportZip() throws Exception {
        List<CompletableFuture<File>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int mod = i;
            CompletableFuture<File> future = CompletableFuture.supplyAsync(() -> {
                        List<Music> list = musicMapper.selectByMod(mod);
                        File file = new File("music_" + mod + ".xlsx");
                        EasyExcel.write(file, MusicExcelDTO.class)
                                .sheet("音乐数据")
                                .doWrite(convert(list));
                        return file;
                    },
                    excelExecutor
            );
            futures.add(future);
        }
        List<File> files = futures.stream()
                .map(CompletableFuture::join)
                .toList();
        File zipFile = new File("music.zip");
        ZipUtil.zip(zipFile, true, files.toArray(new File[0]));
        return zipFile;
    }

    private List<MusicExcelDTO> convert(List<Music> list) {
        return list.stream()
                .map(item -> {
                    MusicExcelDTO dto = new MusicExcelDTO();
                    dto.setMusicName(item.getMusicName());
                    dto.setSingerName(item.getSingerName());
                    dto.setCoverImages(item.getCoverImages());
                    dto.setMusicDesc(item.getMusicDesc());
                    dto.setAlbumTitle(item.getAlbumTitle());
                    dto.setReleaseDate(item.getReleaseDate());
                    dto.setCreateTime(item.getCreateTime());
                    dto.setUpdateTime(item.getUpdateTime());
                    dto.setTypeId(item.getTypeId());
                    return dto;
                }).toList();
    }

    public void uploadZip(MultipartFile multipartFile) throws Exception {
        File zip = File.createTempFile("music", ".zip");
        multipartFile.transferTo(zip);
        File dir = new File("temp/music");
        ZipUtil.unzip(zip, dir);
        List<File> files = FileUtil.loopFiles(dir, pathname -> pathname.getName().endsWith(".xlsx"));
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (File file : files) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        EasyExcel.read(file, MusicExcelDTO.class, new MusicExcelListener(musicMapper))
                                .sheet()
                                .doRead();
                    },
                    excelExecutor
            );
            futures.add(future);
        }
        futures.forEach(CompletableFuture::join);
    }
}