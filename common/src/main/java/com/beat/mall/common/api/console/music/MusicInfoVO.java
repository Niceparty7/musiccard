package com.beat.mall.common.api.console.music;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MusicInfoVO {
    private List<String> coverImages;
    private String musicName;
    private String singerName;
    private String albumTitle;
    private String releaseDate;
    private String musicDesc;
    private String createTime;
    private String updateTime;
    private Integer typeId;
    private String typeName;
    private String typeImage;
    private List<String> tags;
}
