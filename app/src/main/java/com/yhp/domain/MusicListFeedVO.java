package com.yhp.domain;

import java.util.List;

/**
 * 音乐卡片瀑布流
 */
public class MusicListFeedVO {
    private List<MusicListVO> list;

    public List<MusicListVO> getList() {
        return list;
    }

    public void setList(List<MusicListVO> list) {
        this.list = list;
    }
}
