package com.beat.mall.common.api.app.music;

import lombok.Data;
import lombok.experimental.Accessors;

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
