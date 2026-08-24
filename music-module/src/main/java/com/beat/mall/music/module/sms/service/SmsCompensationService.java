package com.beat.mall.music.module.sms.service;

import com.beat.mall.music.module.sms.kafka.config.SmsKafkaProperties;
import com.beat.mall.music.module.sms.kafka.producer.SmsTaskProducer;
import com.beat.mall.music.module.sms.mongo.document.SmsMessageBackup;
import com.beat.mall.music.module.sms.mongo.service.SmsMessageBackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsCompensationService {
    private final SmsMessageBackupService backupService;
    private final SmsTaskProducer producer;
    private final SmsKafkaProperties properties;

    public void compensateOnce() {
        List<SmsMessageBackup> tasks = backupService.findRecoverable();
        for (SmsMessageBackup task : tasks) {
            int retryCount = task.getRetryCount() == null ? 0 : task.getRetryCount();
            if (retryCount > properties.getMaxRetryCount()) {
                if (backupService.markFinalFromCompensation(task)) {
                    publishDeadLetter(task);
                }
                continue;
            }
            if (!backupService.resetForRepublish(task)) {
                continue;
            }
            try {
                producer.publish(backupService.toMessage(task));
                log.info("sms compensation republished, taskId={}, previousStatus={}",
                        task.getId(), task.getStatus());
            } catch (Exception e) {
                log.error("sms compensation publish failed, taskId={}", task.getId(), e);
            }
        }
    }

    private void publishDeadLetter(SmsMessageBackup task) {
        try {
            producer.publishToDeadLetter(backupService.toMessage(task));
        } catch (Exception e) {
            log.error("sms compensation dead letter publish failed, taskId={}", task.getId(), e);
        }
    }
}
