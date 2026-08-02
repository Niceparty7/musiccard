package com.beat.mall.console.domain.music;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 音乐卡片列表
 */
@Data
@Accessors(chain = true)
public class MusicListFeedVO {
    private List<MusicListVO> list;
    private Long total;
    private Integer pageSize;
}
