package top.yuhanpeng.musiccard.module.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.yuhanpeng.musiccard.module.entity.Music;
import top.yuhanpeng.musiccard.module.mapper.MusicMapper;
import top.yuhanpeng.musiccard.module.service.MusicService;

@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {
    @Override
    public IPage<Music> getAllMusic(Integer page, Integer pageSize, String keyword) {
        IPage<Music> musicPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Music> musicLambdaQueryWrapper = new LambdaQueryWrapper<>();
        musicLambdaQueryWrapper.like(
                StringUtils.hasText(keyword),
                Music::getMusicName,
                keyword
        );
        musicLambdaQueryWrapper.orderByAsc(Music::getId);
        IPage<Music> result = null;
        result = baseMapper.selectPage(musicPage, musicLambdaQueryWrapper);
        return result;
    }
}