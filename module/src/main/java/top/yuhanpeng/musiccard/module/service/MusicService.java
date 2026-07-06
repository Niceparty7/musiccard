package top.yuhanpeng.musiccard.module.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.yuhanpeng.musiccard.module.entity.Music;
import top.yuhanpeng.musiccard.module.mapper.MusicMapper;

import java.util.List;

@Service
public class MusicService {
    @Resource
    private MusicMapper musicMapper;

    public Music getMusicById(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        Music music = musicMapper.getById(id);
        if (music == null) {
            throw new RuntimeException("music is null!");
        }
        return music;
    }

    public List<Music> getAllMusicInfo(Integer page, Integer pageSize, String keyword) {
        return musicMapper.getAllMusic((page - 1) * pageSize, pageSize, keyword);
    }

    public Long getTotal(String keyword) {
        return musicMapper.countTotal(keyword);
    }

    public Long edit(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate)
            throws Exception {
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Music music = new Music();
        music.setCoverImages(coverImages)
                .setMusicName(musicName)
                .setSingerName(singerName)
                .setMusicDesc(musicDesc)
                .setAlbumTitle(albumTitle)
                .setReleaseDate(releaseDate)
                .setCreateTime(timeStamp)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0);
        Long resId = id;
        if (id != null) {
            if (musicMapper.getById(id) == null) {
                throw new RuntimeException("cannot find the id");
            }
            music.setId(id);
            musicMapper.update(music);
        } else {
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
            resId = music.getId();
        }
        return resId;
    }

    public Integer deleteMusic(Long id) throws Exception {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        return musicMapper.delete(timeStamp, id);
    }
}