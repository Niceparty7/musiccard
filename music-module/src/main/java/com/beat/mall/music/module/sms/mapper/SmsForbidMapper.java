package com.beat.mall.music.module.sms.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SmsForbidMapper {

    @Select("SELECT COUNT(*) FROM sms_forbid WHERE phone=#{phone} AND is_deleted=0 " +
            "AND begin_time<=#{now} AND end_time>#{now}")
    Integer countActive(@Param("phone") String phone, @Param("now") Integer now);

    @Insert("INSERT INTO sms_forbid(phone,begin_time,end_time,reason,create_time,update_time,is_deleted) " +
            "VALUES(#{phone},#{beginTime},#{endTime},#{reason},#{now},#{now},0) " +
            "ON DUPLICATE KEY UPDATE begin_time=VALUES(begin_time)," +
            "end_time=GREATEST(end_time,VALUES(end_time)),reason=VALUES(reason)," +
            "update_time=VALUES(update_time),is_deleted=0")
    Integer insertOrExtend(@Param("phone") String phone,
                           @Param("beginTime") Integer beginTime,
                           @Param("endTime") Integer endTime,
                           @Param("reason") String reason,
                           @Param("now") Integer now);
}
