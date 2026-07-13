package top.yuhanpeng.musiccard.module.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Category {
    private Long id;
    private String typeName;
    private String typeImage;
    private String typeDesc;
    private Integer createTime;
    private Integer updateTime = (int) (System.currentTimeMillis() / 1000);
    private Integer isDeleted;
}