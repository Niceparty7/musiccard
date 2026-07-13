package top.yuhanpeng.musiccard.module.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.yuhanpeng.musiccard.module.entity.Category;

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
}