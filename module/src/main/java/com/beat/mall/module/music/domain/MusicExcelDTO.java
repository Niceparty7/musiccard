package com.beat.mall.module.music.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class MusicExcelDTO {
    @ExcelProperty("coverImages")
    private String coverImages;
    @ExcelProperty("musicName")
    private String musicName;
    @ExcelProperty("singerName")
    private String singerName;
    @ExcelProperty("musicDesc")
    private String musicDesc;
    @ExcelProperty("albumTitle")
    private String albumTitle;
    @ExcelProperty("releaseDate")
    private String releaseDate;
    @ExcelProperty("createTime")
    private Integer createTime;
    @ExcelProperty("updateTime")
    private Integer updateTime = (int) (System.currentTimeMillis() / 1000);
    @ExcelProperty("isDeleted")
    private Integer isDeleted;
    @ExcelProperty("typeId")
    private Integer typeId;
}