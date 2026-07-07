package top.yuhanpeng.musiccard.module.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.yuhanpeng.musiccard.module.entity.Music;
import top.yuhanpeng.musiccard.module.mapper.MusicMapper;

@Service
public class MusicService {
    @Resource
    private MusicMapper musicMapper;

    public Music getById(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        Music music = musicMapper.selectById(id);
        if (music == null) {
            throw new RuntimeException("music is null!");
        }
        return music;
    }

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
            result = musicMapper.selectPage(musicPage, musicLambdaQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public Long create(String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate)
            throws Exception {
        if (coverImages == null) {
            throw new RuntimeException("coverImages cannot be null!");
        }
        if (musicName == null) {
            throw new RuntimeException("musicName cannot be null!");
        }
        if (singerName == null) {
            throw new RuntimeException("singerName cannot be null!");
        }
        Music music = new Music();
        music.setCoverImages(coverImages)
                .setMusicName(musicName)
                .setSingerName(singerName)
                .setMusicDesc(musicDesc)
                .setAlbumTitle(albumTitle)
                .setReleaseDate(releaseDate);
        musicMapper.insert(music);
        Long resId = music.getId();
        return resId;
    }

    public Long update(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate)
            throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        Music music = new Music();
        music.setId(id)
                .setCoverImages(coverImages)
                .setMusicName(musicName)
                .setSingerName(singerName)
                .setMusicDesc(musicDesc)
                .setAlbumTitle(albumTitle)
                .setReleaseDate(releaseDate);
        if (getById(id) == null) {
            throw new RuntimeException("cannot find the id");
        }
        Long affectedRows = (long) musicMapper.updateById(music);
        return affectedRows;
    }

    public Long edit(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate)
            throws Exception {
        Long res;
        if (id != null) {
            res = update(id, coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate);
            if (res == 0) {
                throw new RuntimeException("update fail!");
            }
        } else {
            res = create(coverImages, musicName, singerName, musicDesc, albumTitle, releaseDate);
            if (res == null) {
                throw new RuntimeException("create fail!");
            }
        }
        return res;
    }

    public Integer delete(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        return musicMapper.deleteById(id);
    }
}