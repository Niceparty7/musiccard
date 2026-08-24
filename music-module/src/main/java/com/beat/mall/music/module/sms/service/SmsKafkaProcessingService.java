package com.beat.mall.music.module.sms.service;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.music.module.sms.kafka.config.SmsKafkaProperties;
import com.beat.mall.music.module.sms.kafka.producer.SmsTaskProducer;
import com.beat.mall.music.module.sms.mongo.document.SmsMessageBackup;
import com.beat.mall.music.module.sms.mongo.service.SmsMessageBackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsKafkaProcessingService {
    private final SmsMessageBackupService backupService;
    private final BaseSmsService baseSmsService;
    private final SmsLogService smsLogService;
    private final SmsTaskProducer producer;
    private final SmsKafkaProperties properties;
    @Qualifier("smsTaskExecutor")
    private final Executor smsTaskExecutor;

    public void submit(List<Long> taskIds) {
        for (Long taskId : taskIds) {
            try {
                smsTaskExecutor.execute(() -> process(taskId));
            } catch (RejectedExecutionException e) {
                log.warn("sms task executor is full, task remains pending, taskId={}", taskId);
            }
        }
    }

    public void process(Long taskId) {
        String claimToken = UUID.randomUUID().toString();
        SmsMessageBackup task = backupService.claimForProcessing(taskId, claimToken);
        if (task == null) {
            return;
        }
        int attemptCount = task.getRetryCount() + 1;
        SmsSendResultDTO result;
        try {
            result = baseSmsService.doSend(task.getPhone(), task.getVerifyCode());
        } catch (Exception e) {
            log.error("sms kafka task execution failed, taskId={}", taskId, e);
            result = SmsSendResultDTO.fail("SEND_FAIL", e.getMessage());
        }
        smsLogService.saveLog(taskId, task.getPhone(), baseSmsService.buildSmsContent(task.getVerifyCode()),
                result, BaseSmsService.SEND_TYPE_ASYNC, (short) attemptCount);
        if (result.isOk()) {
            backupService.markSuccess(taskId, claimToken, result);
            return;
        }
        handleFailure(task, claimToken, result, attemptCount);
    }

    private void handleFailure(SmsMessageBackup task, String claimToken,
                               SmsSendResultDTO result, int retryCount) {
        String errorCode = result.getErrorCode();
        if (isRetryable(errorCode) && retryCount <= properties.getMaxRetryCount()) {
            Instant nextRetryAt = Instant.now()
                    .plusSeconds((long) properties.getRetryDelaySeconds() * retryCount);
            backupService.markRetryWait(task.getId(), claimToken, retryCount,
                    nextRetryAt, errorCode, result.getErrorMessage());
            return;
        }
        backupService.markFinalFailed(task.getId(), claimToken, retryCount,
                errorCode, result.getErrorMessage());
        try {
            producer.publishToDeadLetter(backupService.toMessage(task));
        } catch (Exception e) {
            log.error("sms dead letter publish failed, taskId={}", task.getId(), e);
        }
    }

    private boolean isRetryable(String errorCode) {
        return "SEND_FAIL".equals(errorCode) || "biz.FREQUENCY".equals(errorCode);
    }
}
