package com.beat.mall.music.module.sms.mongo.service;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.music.module.sms.kafka.config.SmsKafkaProperties;
import com.beat.mall.music.module.sms.kafka.model.SmsTaskMessage;
import com.beat.mall.music.module.sms.mongo.document.SmsMessageBackup;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SmsMessageBackupService {
    private final MongoTemplate mongoTemplate;
    private final SmsKafkaProperties properties;

    public List<Long> backupBatch(List<ConsumerRecord<String, SmsTaskMessage>> records) {
        Instant now = Instant.now();
        BulkOperations operations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, SmsMessageBackup.class);
        List<ConsumerRecord<String, SmsTaskMessage>> validRecords = records.stream()
                .filter(record -> record.value() != null && record.value().getTaskId() != null)
                .toList();
        for (ConsumerRecord<String, SmsTaskMessage> record : validRecords) {
            SmsTaskMessage message = record.value();
            Instant createdAt = message.getCreatedAt() == null ? now : Instant.ofEpochMilli(message.getCreatedAt());
            Query query = Query.query(Criteria.where("_id").is(message.getTaskId()));
            Update update = new Update()
                    .setOnInsert("version", message.getVersion())
                    .setOnInsert("phone", message.getPhone())
                    .setOnInsert("verifyCode", message.getVerifyCode())
                    .setOnInsert("traceId", message.getTraceId())
                    .setOnInsert("topic", record.topic())
                    .setOnInsert("partition", record.partition())
                    .setOnInsert("offset", record.offset())
                    .setOnInsert("status", SmsMessageBackup.STATUS_PENDING)
                    .setOnInsert("retryCount", 0)
                    .setOnInsert("createdAt", createdAt)
                    .setOnInsert("updatedAt", now)
                    .setOnInsert("expireAt", now.plus(Duration.ofDays(properties.getBackupRetentionDays())));
            operations.upsert(query, update);
        }
        if (!validRecords.isEmpty()) {
            operations.execute();
        }
        return validRecords.stream().map(record -> record.value().getTaskId()).distinct().toList();
    }

    public SmsMessageBackup claimForProcessing(Long taskId, String claimToken) {
        Instant now = Instant.now();
        Criteria ready = new Criteria().orOperator(
                Criteria.where("status").is(SmsMessageBackup.STATUS_PENDING),
                new Criteria().andOperator(
                        Criteria.where("status").is(SmsMessageBackup.STATUS_RETRY_WAIT),
                        Criteria.where("nextRetryAt").lte(now)));
        Query query = Query.query(new Criteria().andOperator(Criteria.where("_id").is(taskId), ready));
        Update update = new Update()
                .set("status", SmsMessageBackup.STATUS_PROCESSING)
                .set("claimToken", claimToken)
                .set("leaseExpireAt", now.plusSeconds(properties.getProcessingLeaseSeconds()))
                .set("updatedAt", now);
        return mongoTemplate.findAndModify(query, update,
                FindAndModifyOptions.options().returnNew(true), SmsMessageBackup.class);
    }

    public void markSuccess(Long taskId, String claimToken, SmsSendResultDTO result) {
        Query query = processingQuery(taskId, claimToken);
        Update update = new Update()
                .set("status", SmsMessageBackup.STATUS_SUCCESS)
                .set("providerRequestId", result.getRequestId())
                .set("updatedAt", Instant.now())
                .unset("claimToken")
                .unset("leaseExpireAt")
                .unset("nextRetryAt")
                .unset("errorCode")
                .unset("errorMessage");
        mongoTemplate.updateFirst(query, update, SmsMessageBackup.class);
    }

    public void markRetryWait(Long taskId, String claimToken, int retryCount,
                              Instant nextRetryAt, String errorCode, String errorMessage) {
        Update update = failureUpdate(SmsMessageBackup.STATUS_RETRY_WAIT, retryCount, errorCode, errorMessage)
                .set("nextRetryAt", nextRetryAt);
        mongoTemplate.updateFirst(processingQuery(taskId, claimToken), update, SmsMessageBackup.class);
    }

    public void markFinalFailed(Long taskId, String claimToken, int retryCount,
                                String errorCode, String errorMessage) {
        mongoTemplate.updateFirst(processingQuery(taskId, claimToken),
                failureUpdate(SmsMessageBackup.STATUS_FAILED_FINAL, retryCount, errorCode, errorMessage)
                        .unset("nextRetryAt"),
                SmsMessageBackup.class);
    }

    public List<SmsMessageBackup> findRecoverable() {
        Instant now = Instant.now();
        Instant pendingBefore = now.minusSeconds(properties.getPendingTimeoutSeconds());
        Criteria recoverable = new Criteria().orOperator(
                new Criteria().andOperator(
                        Criteria.where("status").is(SmsMessageBackup.STATUS_PENDING),
                        Criteria.where("updatedAt").lte(pendingBefore)),
                new Criteria().andOperator(
                        Criteria.where("status").is(SmsMessageBackup.STATUS_RETRY_WAIT),
                        Criteria.where("nextRetryAt").lte(now)),
                new Criteria().andOperator(
                        Criteria.where("status").is(SmsMessageBackup.STATUS_PROCESSING),
                        Criteria.where("leaseExpireAt").lte(now)));
        Query query = Query.query(recoverable)
                .with(Sort.by(Sort.Direction.ASC, "updatedAt"))
                .limit(properties.getCompensationBatchSize());
        return mongoTemplate.find(query, SmsMessageBackup.class);
    }

    public boolean resetForRepublish(SmsMessageBackup task) {
        Query query = Query.query(new Criteria().andOperator(
                Criteria.where("_id").is(task.getId()),
                Criteria.where("status").is(task.getStatus()),
                Criteria.where("updatedAt").is(task.getUpdatedAt())));
        Update update = new Update()
                .set("status", SmsMessageBackup.STATUS_PENDING)
                .set("updatedAt", Instant.now())
                .unset("claimToken")
                .unset("leaseExpireAt")
                .unset("nextRetryAt");
        return mongoTemplate.updateFirst(query, update, SmsMessageBackup.class).getModifiedCount() == 1;
    }

    public boolean markFinalFromCompensation(SmsMessageBackup task) {
        Query query = Query.query(new Criteria().andOperator(
                Criteria.where("_id").is(task.getId()),
                Criteria.where("status").is(task.getStatus()),
                Criteria.where("updatedAt").is(task.getUpdatedAt())));
        Update update = failureUpdate(SmsMessageBackup.STATUS_FAILED_FINAL,
                task.getRetryCount(), "RETRY_EXHAUSTED", "短信任务超过最大重试次数");
        return mongoTemplate.updateFirst(query, update, SmsMessageBackup.class).getModifiedCount() == 1;
    }

    public SmsTaskMessage toMessage(SmsMessageBackup task) {
        return new SmsTaskMessage()
                .setVersion(task.getVersion())
                .setTaskId(task.getId())
                .setPhone(task.getPhone())
                .setVerifyCode(task.getVerifyCode())
                .setCreatedAt(task.getCreatedAt().toEpochMilli())
                .setTraceId(task.getTraceId());
    }

    private Query processingQuery(Long taskId, String claimToken) {
        return Query.query(new Criteria().andOperator(
                Criteria.where("_id").is(taskId),
                Criteria.where("status").is(SmsMessageBackup.STATUS_PROCESSING),
                Criteria.where("claimToken").is(claimToken)));
    }

    private Update failureUpdate(String status, Integer retryCount, String errorCode, String errorMessage) {
        return new Update()
                .set("status", status)
                .set("retryCount", Objects.requireNonNullElse(retryCount, 0))
                .set("errorCode", errorCode)
                .set("errorMessage", truncate(errorMessage))
                .set("updatedAt", Instant.now())
                .unset("claimToken")
                .unset("leaseExpireAt");
    }

    private String truncate(String value) {
        if (value == null || value.length() <= 500) {
            return value;
        }
        return value.substring(0, 500);
    }
}
