package com.musiccard.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Music {
    private Long id;
    private String coverImages;
    private String musicName;
    private String singerName;
    private String musicDesc;
    private String albumTitle;
    private String releaseDate;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
