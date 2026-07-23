package top.yuhanpeng.musiccard.module.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;
import top.yuhanpeng.musiccard.module.domain.MusicExcelDTO;
import top.yuhanpeng.musiccard.module.entity.Music;
import top.yuhanpeng.musiccard.module.mapper.MusicMapper;

@Slf4j
public class MusicExcelListener extends AnalysisEventListener<MusicExcelDTO> {
    private final MusicMapper musicMapper;

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
        musicMapper.insert(music);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel读取完成");
    }
}