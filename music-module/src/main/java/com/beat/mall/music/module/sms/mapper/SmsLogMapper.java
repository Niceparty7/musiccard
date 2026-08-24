package com.beat.mall.music.module.sms.mapper;

import com.beat.mall.common.entity.sms.SmsLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsLogMapper {

    @Insert("INSERT INTO sms_log(task_id,phone,content,result,biz_id,request_id,error_code,error_message,send_type,status,attempt_count,send_time,create_time,update_time,is_deleted) " +
            "VALUES(#{taskId},#{phone},#{content},#{result},#{bizId},#{requestId},#{errorCode},#{errorMessage},#{sendType},#{status},#{attemptCount},#{sendTime},#{createTime},#{updateTime},0)")
    Long insert(SmsLog log);
}
