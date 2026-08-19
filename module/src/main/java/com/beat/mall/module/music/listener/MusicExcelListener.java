package com.beat.mall.module.music.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.beat.mall.module.music.domain.MusicExcelDTO;
import com.beat.mall.common.entity.music.Music;
import com.beat.mall.module.music.mapper.MusicMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MusicExcelListener extends AnalysisEventListener<MusicExcelDTO> {
    private final MusicMapper musicMapper;
    private final List<Music> list = new ArrayList<>();

    public MusicExcelListener(MusicMapper musicMapper) {
        this.musicMapper = musicMapper;
    }

    @Override
    public void invoke(MusicExcelDTO data, AnalysisContext context) {
        Music music = new Music()
                .setCoverImages(data.getCoverImages())
                .setMusicName(data.getMusicName())
                .setSingerName(data.getSingerName())
                .setMusicDesc(data.getMusicDesc())
                .setAlbumTitle(data.getAlbumTitle())
                .setReleaseDate(data.getReleaseDate())
                .setCreateTime(data.getCreateTime())
                .setUpdateTime(data.getUpdateTime())
                .setIsDeleted(0)
                .setTypeId(data.getTypeId());
        list.add(music);
        if (list.size() >= 20) {
            musicMapper.insertBatch(list);
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!list.isEmpty()) {
            musicMapper.insertBatch(list);
        }
        log.info("Excel读取完成");
    }
}