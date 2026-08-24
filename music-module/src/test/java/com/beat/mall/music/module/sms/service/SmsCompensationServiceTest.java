package com.beat.mall.music.module.sms.service;

import com.beat.mall.music.module.sms.kafka.config.SmsKafkaProperties;
import com.beat.mall.music.module.sms.kafka.model.SmsTaskMessage;
import com.beat.mall.music.module.sms.kafka.producer.SmsTaskProducer;
import com.beat.mall.music.module.sms.mongo.document.SmsMessageBackup;
import com.beat.mall.music.module.sms.mongo.service.SmsMessageBackupService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsCompensationServiceTest {

    @Test
    void shouldRepublishRecoverableTask() {
        SmsMessageBackupService backupService = mock(SmsMessageBackupService.class);
        SmsTaskProducer producer = mock(SmsTaskProducer.class);
        SmsKafkaProperties properties = new SmsKafkaProperties();
        properties.setMaxRetryCount(2);
        SmsMessageBackup task = new SmsMessageBackup()
                .setId(1L)
                .setStatus(SmsMessageBackup.STATUS_RETRY_WAIT)
                .setRetryCount(1)
                .setUpdatedAt(Instant.now());
        SmsTaskMessage message = new SmsTaskMessage().setTaskId(1L).setPhone("13800000000");
        when(backupService.findRecoverable()).thenReturn(List.of(task));
        when(backupService.resetForRepublish(task)).thenReturn(true);
        when(backupService.toMessage(task)).thenReturn(message);

        new SmsCompensationService(backupService, producer, properties).compensateOnce();

        verify(producer).publish(message);
    }
}
