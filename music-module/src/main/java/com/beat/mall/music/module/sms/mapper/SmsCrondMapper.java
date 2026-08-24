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

    /**
     * MySQL 在一条 UPDATE 中完成限量认领；不同实例对相同行加锁后只会有一个实例更新成功。
     */
    @Update("UPDATE sms_crond SET status=1,lock_owner=#{nodeId},claim_token=#{claimToken}," +
            "claim_time=#{now},lease_expire_time=#{leaseExpireTime},update_time=#{now} " +
            "WHERE is_deleted=0 AND (status=0 OR (status=3 AND next_retry_time<=#{now})) " +
            "ORDER BY id ASC LIMIT #{limit}")
    int claimReadyTasks(@Param("nodeId") String nodeId, @Param("claimToken") String claimToken,
                        @Param("now") int now, @Param("leaseExpireTime") int leaseExpireTime,
                        @Param("limit") int limit);

    @Select("SELECT * FROM sms_crond WHERE is_deleted=0 AND status=1 AND lock_owner=#{nodeId} " +
            "AND claim_token=#{claimToken} ORDER BY id ASC")
    List<SmsCrond> selectClaimedTasks(@Param("nodeId") String nodeId, @Param("claimToken") String claimToken);

    @Update("UPDATE sms_crond SET status=2,send_time=#{now},next_retry_time=NULL,error_message=NULL," +
            "lock_owner=NULL,claim_token=NULL,claim_time=NULL,lease_expire_time=NULL,update_time=#{now} " +
            "WHERE id=#{id} AND status=1 AND claim_token=#{claimToken} AND is_deleted=0")
    int markSuccess(@Param("id") Long id, @Param("claimToken") String claimToken, @Param("now") int now);

    @Update("UPDATE sms_crond SET status=3,retry_count=#{retryCount},next_retry_time=#{nextRetryTime}," +
            "error_message=#{errorMessage},lock_owner=NULL,claim_token=NULL,claim_time=NULL,lease_expire_time=NULL," +
            "update_time=#{now} WHERE id=#{id} AND status=1 AND claim_token=#{claimToken} AND is_deleted=0")
    int markRetryWait(@Param("id") Long id, @Param("retryCount") short retryCount,
                      @Param("claimToken") String claimToken, @Param("nextRetryTime") int nextRetryTime, @Param("errorMessage") String errorMessage,
                      @Param("now") int now);

    @Update("UPDATE sms_crond SET status=4,retry_count=#{retryCount},send_time=#{now},error_message=#{errorMessage}," +
            "lock_owner=NULL,claim_token=NULL,claim_time=NULL,lease_expire_time=NULL,update_time=#{now} " +
            "WHERE id=#{id} AND status=1 AND claim_token=#{claimToken} AND is_deleted=0")
    int markFinalFailed(@Param("id") Long id, @Param("retryCount") short retryCount,
                        @Param("claimToken") String claimToken, @Param("errorMessage") String errorMessage, @Param("now") int now);

    @Update("UPDATE sms_crond SET status=0,error_message=#{errorMessage},lock_owner=NULL,claim_token=NULL," +
            "claim_time=NULL,lease_expire_time=NULL,update_time=#{now} " +
            "WHERE id=#{id} AND status=1 AND claim_token=#{claimToken} AND is_deleted=0")
    int returnToPending(@Param("id") Long id, @Param("claimToken") String claimToken,
                        @Param("errorMessage") String errorMessage, @Param("now") int now);

    @Update("UPDATE sms_crond SET status=3,next_retry_time=#{now},error_message='任务租约过期，等待重新认领'," +
            "lock_owner=NULL,claim_token=NULL,claim_time=NULL,lease_expire_time=NULL,update_time=#{now} " +
            "WHERE status=1 AND is_deleted=0 AND lease_expire_time<#{now}")
    int recoverExpiredLease(@Param("now") int now);
}
