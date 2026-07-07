package top.yuhanpeng.musiccard.module.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import top.yuhanpeng.musiccard.module.entity.Music;

public interface MusicService extends IService<Music> {
    IPage<Music> getAllMusic(Integer page, Integer pageSize, String keyword);
}