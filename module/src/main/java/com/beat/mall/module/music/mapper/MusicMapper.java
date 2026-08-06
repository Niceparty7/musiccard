package com.beat.mall.module.music.mapper;

import com.beat.mall.module.music.entity.Music;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MusicMapper {
    @Select("select * from music where id=#{id} and is_deleted=0")
    Music getById(@Param("id") Long id);

    @Select("select * from music where id=#{id}")
    Music extractById(@Param("id") Long id);

    @Select("select * from music where is_deleted=0")
    List<Music> getAllMusicList();

    List<Music> getAllMusic(@Param("offSet") Integer offSet, @Param("pageSize") Integer pageSize,
                            @Param("keyword") String keyword);

    List<Music> getAllMusicList2(@Param("offSet") Integer offSet, @Param("pageSize") Integer pageSize, @Param("musicName") String musicName,
                                          @Param("typeName") String typeName, @Param("tagName") String tagName);

    Integer update(@Param("music") Music music);

    Long insert(@Param("music") Music music);

    Integer delete(@Param("time") Integer time, @Param("id") Long id);

    Long countTotal(@Param("musicName") String musicName, @Param("typeName") String typeName, @Param("tagName") String tagName);

    @Select("select count(*) from music where type_id=#{typeId} and is_deleted=0")
    Long getByTypeId(@Param("typeId") Long typeId);

    Integer insertBatch(@Param("list") List<Music> list);

    @Select("select * from music where id % 10 = #{mod}")
    List<Music> selectByMod(@Param("mod") Integer mod);
}