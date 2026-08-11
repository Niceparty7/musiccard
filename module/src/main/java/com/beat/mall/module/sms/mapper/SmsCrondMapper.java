package com.beat.mall.module.sms.mapper;

import com.beat.mall.module.sms.entity.SmsCrond;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SmsCrondMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO sms_crond(phone,content,status,retry_count,create_time,update_time,is_deleted) " +
            "VALUES(#{phone},#{content},#{status},#{retryCount},#{createTime},#{updateTime},0)")
    Long insert(SmsCrond crond);

    @Update("UPDATE sms_crond SET status=#{status}, retry_count=#{retryCount}, send_time=#{sendTime}, " +
            "error_message=#{errorMessage}, update_time=#{updateTime} WHERE id=#{id} AND is_deleted=0")
    Integer update(SmsCrond crond);

    @Select("SELECT * FROM sms_crond WHERE status=0 AND is_deleted=0 ORDER BY update_time ASC, id ASC LIMIT #{limit}")
    List<SmsCrond> selectPending(@Param("limit") int limit);

    @Select("SELECT * FROM sms_crond WHERE id=#{id} AND is_deleted=0")
    SmsCrond getById(@Param("id") Long id);
}