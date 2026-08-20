package com.beat.mall.common.api.console.music;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MusicListVO {
    private Long id;
    private String wallImage;
    private String musicName;
    private String singerName;
    private String musicDesc;
    private String typeName;
}
