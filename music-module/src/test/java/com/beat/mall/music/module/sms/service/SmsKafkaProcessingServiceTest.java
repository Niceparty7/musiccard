package com.beat.mall.music.module.sms.service;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.music.module.sms.kafka.config.SmsKafkaProperties;
import com.beat.mall.music.module.sms.kafka.producer.SmsTaskProducer;
import com.beat.mall.music.module.sms.mongo.document.SmsMessageBackup;
import com.beat.mall.music.module.sms.mongo.service.SmsMessageBackupService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsKafkaProcessingServiceTest {

    @Test
    void shouldMarkSuccessAfterSmsProviderReturnsOk() {
        Fixture fixture = new Fixture();
        SmsMessageBackup task = fixture.task();
        when(fixture.backupService.claimForProcessing(eq(1L), anyString())).thenReturn(task);
        SmsSendResultDTO result = new SmsSendResultDTO().setOk(true).setRequestId("request-1");
        when(fixture.baseSmsService.doSend(task.getPhone(), task.getVerifyCode())).thenReturn(result);

        fixture.service.submit(List.of(1L));

        verify(fixture.logService).saveLog(eq(1L), eq(task.getPhone()), anyString(), eq(result),
                eq(BaseSmsService.SEND_TYPE_ASYNC), eq((short) 1));
        verify(fixture.backupService).markSuccess(eq(1L), anyString(), eq(result));
        verify(fixture.backupService, never()).markRetryWait(any(), anyString(),
                org.mockito.ArgumentMatchers.anyInt(), any(), anyString(), anyString());
    }

    private static class Fixture {
        private final SmsMessageBackupService backupService = mock(SmsMessageBackupService.class);
        private final BaseSmsService baseSmsService = mock(BaseSmsService.class);
        private final SmsLogService logService = mock(SmsLogService.class);
        private final SmsTaskProducer producer = mock(SmsTaskProducer.class);
        private final SmsKafkaProperties properties = new SmsKafkaProperties();
        private final Executor directExecutor = Runnable::run;
        private final SmsKafkaProcessingService service = new SmsKafkaProcessingService(
                backupService, baseSmsService, logService, producer, properties, directExecutor);

        private Fixture() {
            properties.setMaxRetryCount(2);
            when(baseSmsService.buildSmsContent(anyString())).thenReturn("sms content");
        }

        private SmsMessageBackup task() {
            return new SmsMessageBackup()
                    .setId(1L)
                    .setPhone("13800000000")
                    .setVerifyCode("123456")
                    .setRetryCount(0);
        }
    }
}
