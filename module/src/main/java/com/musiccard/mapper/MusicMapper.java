package com.musiccard.mapper;

import com.musiccard.entity.Music;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MusicMapper {
    @Select("select * from music where id=#{id} and is_deleted=0")
    Music getById(@Param("id") Long id);

    @Select("select * from music where is_deleted=0")
    List<Music> getAllMusic();

    int update(@Param("music") Music music);

    int insert(@Param("music") Music music);

    @Update("update music set is_deleted=1,update_time=#{time} where is_deleted=0 and id=#{id}")
    int delete(@Param("time") Integer time, @Param("id") Long id);
}
