package com.beat.mall.module.category.mapper;

import com.beat.mall.module.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 音乐类型表
 *
 * @author YHP
 */
@Mapper
public interface CategoryMapper {
    @Select("select * from category where id = #{id} and is_deleted = 0")
    Category getById(@Param("id") Long id);

    @Select("select * from category where id = #{id}")
    Category extractById(@Param("id") Long id);

    @Select("select * from category where is_deleted = 0")
    List<Category> getAllCategory();

    Integer update(@Param("category") Category category);

    Long insert(@Param("category") Category category);

    Integer delete(@Param("time") Integer time, @Param("id") Long id);

    @Select("select id from category where parent_id=#{id}")
    List<Long> getChildrenById(@Param("id") Long id);

    @Select("select * from category where is_deleted=0 and type_name like concat('%',#{keyword},'%')")
    List<Category> getCategoryByKeyword(@Param("keyword") String keyword);
}