package com.beat.mall.music.module.sms.kafka.consumer;

import com.beat.mall.music.module.sms.kafka.model.SmsTaskMessage;
import com.beat.mall.music.module.sms.mongo.service.SmsMessageBackupService;
import com.beat.mall.music.module.sms.service.SmsKafkaProcessingService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SmsBatchConsumerTest {

    @Test
    void shouldBackupBeforeAcknowledgingAndDispatching() {
        SmsMessageBackupService backupService = mock(SmsMessageBackupService.class);
        SmsKafkaProcessingService processingService = mock(SmsKafkaProcessingService.class);
        Acknowledgment acknowledgment = mock(Acknowledgment.class);
        SmsTaskMessage message = new SmsTaskMessage().setTaskId(1L).setPhone("13800000000");
        List<ConsumerRecord<String, SmsTaskMessage>> records = List.of(
                new ConsumerRecord<>("musiccard.sms.send.v1", 0, 1L, message.getPhone(), message));
        when(backupService.backupBatch(records)).thenReturn(List.of(1L));

        new SmsBatchConsumer(backupService, processingService).consume(records, acknowledgment);

        InOrder order = inOrder(backupService, acknowledgment, processingService);
        order.verify(backupService).backupBatch(records);
        order.verify(acknowledgment).acknowledge();
        order.verify(processingService).submit(List.of(1L));
    }
}
