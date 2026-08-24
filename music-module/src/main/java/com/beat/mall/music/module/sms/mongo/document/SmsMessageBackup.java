package com.beat.mall.music.module.sms.mongo.document;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Accessors(chain = true)
@Document("sms_message_backup")
@CompoundIndexes({
        @CompoundIndex(name = "uk_topic_partition_offset", def = "{'topic':1,'partition':1,'offset':1}", unique = true),
        @CompoundIndex(name = "idx_status_next_retry", def = "{'status':1,'nextRetryAt':1}"),
        @CompoundIndex(name = "idx_status_lease_expire", def = "{'status':1,'leaseExpireAt':1}"),
        @CompoundIndex(name = "idx_status_created", def = "{'status':1,'createdAt':1}")
})
public class SmsMessageBackup {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_RETRY_WAIT = "RETRY_WAIT";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED_FINAL = "FAILED_FINAL";

    @Id
    private Long id;
    private String version;
    private String phone;
    private String verifyCode;
    private String traceId;
    private String topic;
    private Integer partition;
    private Long offset;
    private String status;
    private Integer retryCount;
    private Instant nextRetryAt;
    private String claimToken;
    private Instant leaseExpireAt;
    private String providerRequestId;
    private String errorCode;
    private String errorMessage;
    private Instant createdAt;
    private Instant updatedAt;
    @Indexed(name = "idx_expire_at_ttl", expireAfter = "0s")
    private Instant expireAt;
}
