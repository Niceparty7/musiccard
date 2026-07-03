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

    public Music getMusicById(Long id) {
        return musicMapper.getById(id);
    }

    public List<Music> getAllMusicInfo(Integer page, Integer pageSize) {
        return musicMapper.getAllMusic((page - 1) * pageSize, pageSize);
    }

    public Long getTotal() {
        return musicMapper.countTotal();
    }

    public Long createMusic(String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate) {
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
        musicMapper.insert(music);
        return music.getId();
    }

    public int updateMusic(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate) {
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Music music = new Music();
        music.setId(id)
                .setCoverImages(coverImages)
                .setMusicName(musicName)
                .setSingerName(singerName)
                .setMusicDesc(musicDesc)
                .setAlbumTitle(albumTitle)
                .setReleaseDate(releaseDate)
                .setCreateTime(timeStamp)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0);
        return musicMapper.update(music);
    }

    public int deleteMusic(Long id) {
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        return musicMapper.delete(timeStamp, id);
    }
}