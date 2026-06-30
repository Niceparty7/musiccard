package com.yhp.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 音乐详情
 */
@Data
@Accessors(chain = true)
public class MusicInfoVO {
    private String coverImages;
    private String musicName;
    private String singerName;
    private String albumTitle;
    private String releaseDate;
    private String musicDesc;
}
