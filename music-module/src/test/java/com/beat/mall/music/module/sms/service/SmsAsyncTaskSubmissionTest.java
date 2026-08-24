package com.beat.mall.music.module.sms.service;

import com.beat.mall.common.api.sms.SmsTaskSubmitResultDTO;
import com.beat.mall.music.module.sms.config.AliyunSmsProperties;
import com.beat.mall.music.module.sms.kafka.config.SmsKafkaProperties;
import com.beat.mall.music.module.sms.kafka.model.SmsTaskMessage;
import com.beat.mall.music.module.sms.kafka.producer.SmsTaskProducer;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsAsyncTaskSubmissionTest {

    @Test
    void shouldPublishKafkaTaskAfterFrequencyGuardPasses() {
        SmsSendGuardService guard = mock(SmsSendGuardService.class);
        SmsTaskProducer producer = mock(SmsTaskProducer.class);
        when(guard.tryAcquire("13800000000")).thenReturn(true);

        SmsTaskSubmitResultDTO result = newService(guard, producer, true).submitAsyncTask("13800000000");

        assertTrue(result.isAccepted());
        assertTrue(result.getTaskId() > 0);
        assertEquals("PENDING", result.getStatus());
        verify(producer).publish(any(SmsTaskMessage.class));
    }

    @Test
    void shouldNotPublishTaskWhenFrequencyGuardRejects() {
        SmsSendGuardService guard = mock(SmsSendGuardService.class);
        SmsTaskProducer producer = mock(SmsTaskProducer.class);
        when(guard.tryAcquire("13800000000")).thenReturn(false);

        SmsTaskSubmitResultDTO result = newService(guard, producer, true).submitAsyncTask("13800000000");

        assertFalse(result.isAccepted());
        assertEquals("SMS_FORBIDDEN", result.getErrorCode());
        verify(producer, never()).publish(any());
    }

    @Test
    void shouldRejectTaskWhenKafkaPublishFails() {
        SmsSendGuardService guard = mock(SmsSendGuardService.class);
        SmsTaskProducer producer = mock(SmsTaskProducer.class);
        when(guard.tryAcquire("13800000000")).thenReturn(true);
        doThrow(new IllegalStateException("broker unavailable")).when(producer).publish(any());

        SmsTaskSubmitResultDTO result = newService(guard, producer, true).submitAsyncTask("13800000000");

        assertFalse(result.isAccepted());
        assertEquals("MQ_PUBLISH_FAILED", result.getErrorCode());
    }

    private BaseSmsService newService(SmsSendGuardService guard, SmsTaskProducer producer, boolean enabled) {
        AliyunSmsProperties properties = new AliyunSmsProperties();
        SmsKafkaProperties kafkaProperties = new SmsKafkaProperties();
        kafkaProperties.setEnabled(enabled);
        Executor executor = Runnable::run;
        return new BaseSmsService(null, properties, mock(SmsLogService.class), guard,
                producer, kafkaProperties, executor);
    }
}
