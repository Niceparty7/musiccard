package com.beat.mall.music.module.tag.mapper;

import com.beat.mall.common.entity.tag.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TagMapper {

    @Select("SELECT * FROM tag WHERE id = #{id} AND is_deleted = 0")
    Tag getById(@Param("id") Long id);

    @Select("SELECT * FROM tag WHERE id = #{id}")
    Tag extractById(@Param("id") Long id);

    int update(Tag entity);

    int insert(Tag entity);

    @Update("UPDATE tag SET is_deleted = 1, update_time = #{time} WHERE id = #{id} and is_deleted=0")
    int delete(@Param("id") Long id, @Param("time") int time);

    @Select("SELECT * FROM tag WHERE is_deleted = 0")
    List<Tag> getAll();

    @Select("SELECT * FROM tag WHERE tag_name = #{tagName} ")
    Tag extractByTagName(@Param("tagName") String tagName);

    @Select("select id from tag where is_deleted=0 and tag_name like concat('%',#{keyword},'%')")
    List<Long> getTagIdsByKeyword(@Param("keyword") String keyword);
}
