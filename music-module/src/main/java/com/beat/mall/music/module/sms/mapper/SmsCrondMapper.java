package com.beat.mall.music.module.sms.mapper;

import com.beat.mall.common.entity.sms.SmsCrond;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SmsCrondMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO sms_crond(message_id,phone,content,status,retry_count,publish_status,next_retry_time,create_time,update_time,is_deleted) " +
            "VALUES(#{messageId},#{phone},#{content},#{status},#{retryCount},#{publishStatus},#{nextRetryTime},#{createTime},#{updateTime},0)")
    Long insert(SmsCrond crond);

    @Update("UPDATE sms_crond SET status=#{status}, retry_count=#{retryCount}, send_time=#{sendTime}, " +
            "publish_status=#{publishStatus}, next_retry_time=#{nextRetryTime}, " +
            "error_message=#{errorMessage}, update_time=#{updateTime} " +
            "WHERE id=#{id} AND is_deleted=0")
    Integer update(SmsCrond crond);

    @Select("SELECT * FROM sms_crond WHERE status=0 AND is_deleted=0 ORDER BY update_time ASC, id ASC LIMIT #{limit}")
    List<SmsCrond> selectPending(@Param("limit") int limit);

    @Select("SELECT * FROM sms_crond " +
            "WHERE status=0 AND is_deleted=0 AND publish_status IN (0,2) " +
            "AND (next_retry_time IS NULL OR next_retry_time <= UNIX_TIMESTAMP()) " +
            "ORDER BY update_time ASC, id ASC LIMIT #{limit}")
    List<SmsCrond> selectPendingPublish(@Param("limit") int limit);

    @Update("UPDATE sms_crond SET status=3, update_time=#{updateTime} " +
            "WHERE id=#{id} AND status=0 AND is_deleted=0")
    Integer markSending(@Param("id") Long id, @Param("updateTime") Integer updateTime);

    @Update("UPDATE sms_crond SET publish_status=1, next_retry_time=NULL, " +
            "error_message=NULL, update_time=#{updateTime} " +
            "WHERE id=#{id} AND is_deleted=0 AND publish_status IN (0,2)")
    Integer markPublishSuccess(@Param("id") Long id, @Param("updateTime") Integer updateTime);

    @Update("UPDATE sms_crond SET publish_status=2, next_retry_time=#{nextRetryTime}, " +
            "error_message=#{errorMessage}, update_time=#{updateTime} " +
            "WHERE id=#{id} AND is_deleted=0 AND publish_status IN (0,2)")
    Integer markPublishFailed(@Param("id") Long id,
                              @Param("errorMessage") String errorMessage,
                              @Param("nextRetryTime") Integer nextRetryTime,
                              @Param("updateTime") Integer updateTime);

    @Update("UPDATE sms_crond SET status=1, retry_count=#{retryCount}, send_time=#{sendTime}, " +
            "publish_status=1, next_retry_time=NULL, error_message=NULL, update_time=#{sendTime} " +
            "WHERE id=#{id} AND status=3 AND is_deleted=0")
    Integer markSendSuccess(@Param("id") Long id,
                            @Param("retryCount") Short retryCount,
                            @Param("sendTime") Integer sendTime);

    @Update("UPDATE sms_crond SET status=#{status}, retry_count=#{retryCount}, " +
            "publish_status=2, next_retry_time=#{nextRetryTime}, error_message=#{errorMessage}, " +
            "update_time=#{updateTime} WHERE id=#{id} AND status=3 AND is_deleted=0")
    Integer markSendFailed(@Param("id") Long id,
                           @Param("status") Short status,
                           @Param("retryCount") Short retryCount,
                           @Param("nextRetryTime") Integer nextRetryTime,
                           @Param("errorMessage") String errorMessage,
                           @Param("updateTime") Integer updateTime);

    @Select("SELECT * FROM sms_crond WHERE id=#{id} AND is_deleted=0")
    SmsCrond getById(@Param("id") Long id);
}
