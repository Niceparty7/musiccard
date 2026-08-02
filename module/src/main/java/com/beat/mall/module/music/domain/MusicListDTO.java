package com.beat.mall.module.music.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MusicListDTO {
    private Long id;
    private String coverImages;
    private String musicName;
    private String singerName;
    private String musicDesc;
    private String typeName;
}