package top.yuhanpeng.musiccard.module.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MusicCategoryDTO {
    private Long id;
    private String coverImages;
    private String musicName;
    private String singerName;
    private String musicDesc;
    private String albumTitle;
    private String releaseDate;
    private Integer createTime;
    private Integer updateTime;
    private String typeName;
    private String typeImage;
}