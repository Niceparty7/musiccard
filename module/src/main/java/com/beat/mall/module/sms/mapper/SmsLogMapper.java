package com.beat.mall.module.sms.mapper;

import com.beat.mall.common.entity.sms.SmsLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SmsLogMapper {

    @Insert("INSERT INTO sms_log(phone,content,result,biz_id,request_id,error_code,error_message,send_type,status,send_time,create_time,update_time,is_deleted) " +
            "VALUES(#{phone},#{content},#{result},#{bizId},#{requestId},#{errorCode},#{errorMessage},#{sendType},#{status},#{sendTime},#{createTime},#{updateTime},0)")
    Long insert(SmsLog log);

    @Select("SELECT COUNT(*) FROM sms_log WHERE phone=#{phone} AND send_time BETWEEN #{start} AND #{end} AND is_deleted=0")
    Long countByPhoneToday(@Param("phone") String phone, @Param("start") Integer start, @Param("end") Integer end);
}