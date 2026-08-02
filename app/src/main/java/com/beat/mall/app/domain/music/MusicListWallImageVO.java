package com.beat.mall.app.domain.music;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MusicListWallImageVO {
    private String url;
    private Float ar;
}