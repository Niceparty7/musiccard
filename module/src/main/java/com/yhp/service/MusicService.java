package com.yhp.service;

import com.yhp.entity.Music;
import com.yhp.mapper.MusicMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicService {
    @Resource
    private MusicMapper musicMapper;

    public Music getMusicById(Long id) {
        return musicMapper.getById(id);
    }

    public List<Music> getAllMusicInfo() {
        return musicMapper.getAllMusic();
    }

    public int createMusic(String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate) {
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Music music = new Music();
        music.setCoverImages(coverImages);
        music.setMusicName(musicName);
        music.setSingerName(singerName);
        music.setMusicDesc(musicDesc);
        music.setAlbumTitle(albumTitle);
        music.setReleaseDate(releaseDate);
        music.setCreateTime(timeStamp);
        music.setUpdateTime(timeStamp);
        music.setIsDeleted(0);
        return musicMapper.insert(music);
    }

    public int updateMusic(Long id, String coverImages, String musicName, String singerName, String musicDesc, String albumTitle, String releaseDate) {
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        Music music = new Music();
        music.setId(id);
        music.setCoverImages(coverImages);
        music.setMusicName(musicName);
        music.setSingerName(singerName);
        music.setMusicDesc(musicDesc);
        music.setAlbumTitle(albumTitle);
        music.setReleaseDate(releaseDate);
        music.setCreateTime(timeStamp);
        music.setUpdateTime(timeStamp);
        music.setIsDeleted(0);
        return musicMapper.update(music);
    }

    public int deleteMusic(Long id) {
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        return musicMapper.delete(timeStamp, id);
    }

}
