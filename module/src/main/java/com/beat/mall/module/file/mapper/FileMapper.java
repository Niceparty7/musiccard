package com.beat.mall.module.file.mapper;

import com.beat.mall.module.file.entity.File;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FileMapper {
    public Long insert(@Param("file") File file);

    @Update("update file set is_deleted=1 , update_time=#{time} where is_deleted=0 and id=#{id}")
    public Integer delete(@Param("time") Integer time, @Param("id") Long id);
}