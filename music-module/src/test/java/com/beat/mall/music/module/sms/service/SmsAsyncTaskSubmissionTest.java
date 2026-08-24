package com.beat.mall.music.module.sms.service;

import com.beat.mall.common.api.sms.SmsTaskSubmitResultDTO;
import com.beat.mall.common.entity.sms.SmsCrond;
import com.beat.mall.music.module.sms.config.AliyunSmsProperties;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsAsyncTaskSubmissionTest {

    @Test
    void shouldCreatePendingTaskAfterFrequencyGuardPasses() {
        SmsSendGuardService guard = mock(SmsSendGuardService.class);
        SmsCrondService crondService = mock(SmsCrondService.class);
        when(guard.tryAcquire("13800000000")).thenReturn(true);
        when(crondService.create(any(SmsCrond.class))).thenReturn(12L);

        SmsTaskSubmitResultDTO result = newService(guard, crondService).submitAsyncTask("13800000000");

        assertTrue(result.isAccepted());
        assertEquals(12L, result.getTaskId());
        assertEquals("PENDING", result.getStatus());
        verify(crondService).create(any(SmsCrond.class));
    }

    @Test
    void shouldNotCreateTaskWhenFrequencyGuardRejects() {
        SmsSendGuardService guard = mock(SmsSendGuardService.class);
        SmsCrondService crondService = mock(SmsCrondService.class);
        when(guard.tryAcquire("13800000000")).thenReturn(false);

        SmsTaskSubmitResultDTO result = newService(guard, crondService).submitAsyncTask("13800000000");

        assertFalse(result.isAccepted());
        assertEquals("SMS_FORBIDDEN", result.getErrorCode());
        verify(crondService, never()).create(any());
    }

    private BaseSmsService newService(SmsSendGuardService guard, SmsCrondService crondService) {
        AliyunSmsProperties properties = new AliyunSmsProperties();
        Executor executor = Runnable::run;
        return new BaseSmsService(null, properties, mock(SmsLogService.class), guard, crondService, executor);
    }
}
