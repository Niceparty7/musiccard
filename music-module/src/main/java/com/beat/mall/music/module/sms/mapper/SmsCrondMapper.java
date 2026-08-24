package com.beat.mall.music.module.sms.mapper;

import com.beat.mall.common.entity.sms.SmsCrond;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SmsCrondMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("INSERT INTO sms_crond(phone,content,status,retry_count,next_retry_time,error_message,create_time,update_time,is_deleted) " +
            "VALUES(#{phone},#{content},#{status},#{retryCount},#{nextRetryTime},#{errorMessage},#{createTime},#{updateTime},0)")
    int insert(SmsCrond task);

    @Select("SELECT * FROM sms_crond WHERE is_deleted=0 AND " +
            "(status=0 OR (status=3 AND next_retry_time<=#{now})) ORDER BY id ASC LIMIT #{limit}")
    List<SmsCrond> selectReadyTasks(@Param("now") int now, @Param("limit") int limit);

    @Update("UPDATE sms_crond SET status=1,update_time=#{now} WHERE id=#{id} AND is_deleted=0 " +
            "AND (status=0 OR (status=3 AND next_retry_time<=#{now}))")
    int markSending(@Param("id") Long id, @Param("now") int now);

    @Update("UPDATE sms_crond SET status=2,send_time=#{now},next_retry_time=NULL,error_message=NULL,update_time=#{now} " +
            "WHERE id=#{id} AND status=1 AND is_deleted=0")
    int markSuccess(@Param("id") Long id, @Param("now") int now);

    @Update("UPDATE sms_crond SET status=3,retry_count=#{retryCount},next_retry_time=#{nextRetryTime}," +
            "error_message=#{errorMessage},update_time=#{now} WHERE id=#{id} AND status=1 AND is_deleted=0")
    int markRetryWait(@Param("id") Long id, @Param("retryCount") short retryCount,
                      @Param("nextRetryTime") int nextRetryTime, @Param("errorMessage") String errorMessage,
                      @Param("now") int now);

    @Update("UPDATE sms_crond SET status=4,retry_count=#{retryCount},send_time=#{now},error_message=#{errorMessage}," +
            "update_time=#{now} WHERE id=#{id} AND status=1 AND is_deleted=0")
    int markFinalFailed(@Param("id") Long id, @Param("retryCount") short retryCount,
                        @Param("errorMessage") String errorMessage, @Param("now") int now);

    @Update("UPDATE sms_crond SET status=0,error_message=#{errorMessage},update_time=#{now} " +
            "WHERE id=#{id} AND status=1 AND is_deleted=0")
    int returnToPending(@Param("id") Long id, @Param("errorMessage") String errorMessage, @Param("now") int now);

    @Update("UPDATE sms_crond SET status=3,next_retry_time=#{now},error_message='发送线程执行超时，等待重试',update_time=#{now} " +
            "WHERE status=1 AND is_deleted=0 AND update_time<#{deadline}")
    int recoverTimeoutSending(@Param("deadline") int deadline, @Param("now") int now);
}
