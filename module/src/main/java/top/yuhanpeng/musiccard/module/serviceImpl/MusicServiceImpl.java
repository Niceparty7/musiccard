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
        try {
            result = baseMapper.selectPage(musicPage, musicLambdaQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public Long edit(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate)
            throws Exception {
        Boolean success;
        Music music = new Music();
        music.setCoverImages(coverImages)
                .setMusicName(musicName)
                .setSingerName(singerName)
                .setMusicDesc(musicDesc)
                .setAlbumTitle(albumTitle)
                .setReleaseDate(releaseDate);
        Long resId = null;
        if (id != null) {
            if (getById(id) == null) {
                throw new RuntimeException("cannot find the id!");
            }
            music.setId(id);
            success = updateById(music);
            if (!success) {
                throw new RuntimeException("update fail!");
            }
        } else {
            if (coverImages == null || musicName == null || singerName == null) {
                throw new RuntimeException("coverImages,musicName,singerName cannot be null!");
            }
            success = save(music);
            if (!success) {
                throw new RuntimeException("create fail!");
            }
            resId = music.getId();
        }
        return resId;
    }
}