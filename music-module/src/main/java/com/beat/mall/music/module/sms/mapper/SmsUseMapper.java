package com.beat.mall.music.module.sms.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SmsUseMapper {

    @Insert("INSERT INTO sms_use(phone,minute_start,request_count,create_time,update_time) " +
            "VALUES(#{phone},#{minuteStart},1,#{now},#{now}) " +
            "ON DUPLICATE KEY UPDATE request_count=request_count+1,update_time=VALUES(update_time)")
    Integer increment(@Param("phone") String phone,
                      @Param("minuteStart") Integer minuteStart,
                      @Param("now") Integer now);

    @Select("SELECT request_count FROM sms_use WHERE phone=#{phone} AND minute_start=#{minuteStart}")
    Integer selectRequestCount(@Param("phone") String phone,
                               @Param("minuteStart") Integer minuteStart);
}
