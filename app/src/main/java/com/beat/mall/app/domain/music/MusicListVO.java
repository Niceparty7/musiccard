package com.beat.mall.app.domain.music;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 音乐卡片
 */
@Data
@Accessors(chain = true)
public class MusicListVO {
    private Long id;
    private MusicListWallImageVO wallImage;
    private String musicName;
    private String singerName;
    private String musicDesc;
    private String typeName;
}