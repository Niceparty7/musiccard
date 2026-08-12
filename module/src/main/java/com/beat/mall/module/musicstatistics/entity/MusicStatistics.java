package com.beat.mall.module.musicstatistics.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MusicStatistics {
    private Long id;
    private String year;
    private String month;
    private String day;
    private Integer musicCount;
    private Integer createTime;
    private Integer updateTime;
    private Integer isDeleted;
}