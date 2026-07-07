package top.yuhanpeng.musiccard.module.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("music")
public class Music {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String coverImages;
    private String musicName;
    private String singerName;
    private String musicDesc;
    private String albumTitle;
    private String releaseDate;
    @TableField(fill = FieldFill.INSERT)
    private Integer createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updateTime;
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;
}
