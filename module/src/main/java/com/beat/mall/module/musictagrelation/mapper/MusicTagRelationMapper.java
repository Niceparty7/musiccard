package com.beat.mall.module.musictagrelation.mapper;

import com.beat.mall.module.musictagrelation.entity.MusicTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MusicTagRelationMapper {

    @Select("SELECT * FROM music_tag_relation WHERE music_id = #{musicId} AND tag_id=#{tagId} AND is_deleted = 0")
    MusicTagRelation getById(@Param("musicId") Long musicId, @Param("tagId") Long tagId);

    @Select("SELECT * FROM music_tag_relation WHERE music_id = #{musicId} AND tag_id=#{tagId}")
    MusicTagRelation extractById(@Param("musicId") Long musicId, @Param("tagId") Long tagId);

    int update(MusicTagRelation entity);

    int insert(MusicTagRelation entity);

    @Update("UPDATE music_tag_relation SET is_deleted = 1, update_time = #{time} WHERE music_id = #{musicId} AND tag_id=#{tagId}")
    int delete(@Param("musicId") Long musicId, @Param("tagId") Long tagId, @Param("time") int time);

    @Select("SELECT * FROM music_tag_relation WHERE is_deleted = 0")
    List<MusicTagRelation> getAll();

    @Select("select distinct tag_id from music_tag_relation where music_id=#{musicId} and is_deleted=0")
    List<Long> getTagsByMusicId(@Param("musicId") Long musicId);

    @Select("select distinct music_id from music_tag_relation where tag_id=#{tagId} and is_deleted=0")
    List<Long> getMusicsByTagId(@Param("tagId") Long tagId);
}