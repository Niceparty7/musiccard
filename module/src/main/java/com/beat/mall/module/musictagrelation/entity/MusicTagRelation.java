package com.beat.mall.module.musictagrelation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("music_tag_relation")
public class MusicTagRelation {
    private Long musicId;
    private Long tagId;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}