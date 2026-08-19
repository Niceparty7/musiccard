package com.beat.mall.common.entity.file;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class File {
    private Long id;
    private Type type;
    private String url;
    private Integer createTime;
    private Integer updateTime = (int) (System.currentTimeMillis() / 1000);
    private Integer isDeleted;
}
