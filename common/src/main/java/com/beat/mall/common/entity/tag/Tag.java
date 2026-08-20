package com.beat.mall.common.entity.tag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("tag")
public class Tag {
    @TableId(type = IdType.AUTO, value = "id")
    private Long id;
    private String tagName;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
    private String tagDesc;
}
