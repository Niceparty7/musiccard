package com.yhp.domain;

/**
 * 音乐卡片
 */
public class MusicListVO {
    private Long id;
    private String wallImage;
    private String musicName;
    private String singerName;
    private String musicDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWallImage() {
        return wallImage;
    }

    public void setWallImage(String wallImage) {
        this.wallImage = wallImage;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getMusicDesc() {
        return musicDesc;
    }

    public void setMusicDesc(String musicDesc) {
        this.musicDesc = musicDesc;
    }
}