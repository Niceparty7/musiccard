package top.yuhanpeng.musiccard.module.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import top.yuhanpeng.musiccard.module.entity.Music;

public interface MusicService extends IService<Music> {
    IPage<Music> getAllMusic(Integer page, Integer pageSize, String keyword);

    Long edit(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate) throws Exception;
}