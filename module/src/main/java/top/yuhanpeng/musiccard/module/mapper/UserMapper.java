package top.yuhanpeng.musiccard.module.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.yuhanpeng.musiccard.module.entity.User;

/**
 * 用户表
 *
 * @author YHP
 */
@Mapper
public interface UserMapper {
    @Select("select * from user where id = #{id} and is_deleted = 0")
    User getById(@Param("id") Long id);

    @Select("select * from user where id = #{id}")
    User extractById(@Param("id") Long id);

    Integer update(@Param("user") User user);

    Long insert(@Param("user") User user);

    Integer delete(@Param("time") Integer time, @Param("id") Long id);

    @Select("select * from user where phone = #{phone}")
    User extractByPhone(@Param("phone") String phone);
}