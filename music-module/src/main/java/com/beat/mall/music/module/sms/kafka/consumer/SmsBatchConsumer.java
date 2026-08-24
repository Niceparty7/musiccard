package com.beat.mall.music.module.sms.kafka.consumer;

import com.beat.mall.music.module.sms.kafka.model.SmsTaskMessage;
import com.beat.mall.music.module.sms.mongo.service.SmsMessageBackupService;
import com.beat.mall.music.module.sms.service.SmsKafkaProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.sms.kafka", name = "enabled", havingValue = "true")
@Slf4j
public class SmsBatchConsumer {
    private final SmsMessageBackupService backupService;
    private final SmsKafkaProcessingService processingService;

    @KafkaListener(
            topics = "${app.sms.kafka.topic}",
            groupId = "${app.sms.kafka.consumer-group}",
            concurrency = "${app.sms.kafka.concurrency:3}")
    public void consume(List<ConsumerRecord<String, SmsTaskMessage>> records, Acknowledgment acknowledgment) {
        List<Long> taskIds = backupService.backupBatch(records);
        acknowledgment.acknowledge();
        processingService.submit(taskIds);
        log.info("sms kafka batch accepted, recordCount={}, taskCount={}", records.size(), taskIds.size());
    }
}
