package com.beat.mall.music.module.music.service;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.excel.EasyExcel;
import com.beat.mall.music.module.annotation.ReadOnly;
import com.beat.mall.music.module.music.domain.MusicExcelDTO;
import com.beat.mall.common.entity.music.Music;
import com.beat.mall.music.module.music.listener.MusicExcelListener;
import com.beat.mall.music.module.music.mapper.MusicMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
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

    @ReadOnly
    public List<Music> getAllMusic(Integer offSet, Integer pageSize, String keyword, String subquery, String subquery2) {
        return musicMapper.getAllMusic(offSet, pageSize, keyword, subquery, subquery2);
    }

    @ReadOnly
    public List<Music> getAllMusicList2(Integer offSet, Integer pageSize, String musicName, String subquery, String subquery2) {
        return musicMapper.getAllMusicList2(offSet, pageSize, musicName, subquery, subquery2);
    }

    @ReadOnly
    public Long countTotal(String musicName, String typeName, String tagName) {
        return musicMapper.countTotal(musicName, typeName, tagName);
    }

    public Integer update(Music music) {
        return musicMapper.update(music);
    }

    public Long insert(Music music) {
        return musicMapper.insert(music);
    }

    public Music extractById(Long id) {
        return musicMapper.extractById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer delete(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        return musicMapper.delete(timeStamp, id);
    }

    @ReadOnly
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
        // 全部中间产物统一落在系统临时目录，与项目/JAR 完全隔离；调用方负责删除返回的 zip（含父临时目录）
        File dir = Files.createTempDirectory("musiccard-export").toFile();
        List<CompletableFuture<File>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int mod = i;
            CompletableFuture<File> future = CompletableFuture.supplyAsync(() -> {
                        List<Music> list = musicMapper.selectByMod(mod);
                        File file = new File(dir, "music_" + mod + ".xlsx");
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
        File zipFile = new File(dir, "music.zip");
        ZipUtil.zip(zipFile, false, files.toArray(new File[0]));
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
        // zip 与解压目录统一落在系统临时目录（java.io.tmpdir），与项目/JAR 完全隔离
        File zip = null;
        File dir = null;
        try {
            zip = File.createTempFile("music", ".zip");
            dir = Files.createTempDirectory("musiccard-unzip").toFile();
            multipartFile.transferTo(zip);
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
        } finally {
            // 无论成功失败，用后即删，避免临时文件堆积；判空防止创建阶段异常导致 NPE
            if (dir != null) {
                FileUtil.del(dir);
            }
            if (zip != null) {
                FileUtil.del(zip);
            }
        }
    }

    @ReadOnly
    public Long countAll() {
        return musicMapper.countAll();
    }
}
