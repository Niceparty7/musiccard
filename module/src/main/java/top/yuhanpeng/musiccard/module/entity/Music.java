package top.yuhanpeng.musiccard.module.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Music {
    private Long id;
    private String coverImages="https://null.com$https://null.com$https://null.com";
    private String musicName="未知";
    private String singerName="未知";
    private String musicDesc="未知";
    private String albumTitle="未知";
    private String releaseDate="未知";
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}
