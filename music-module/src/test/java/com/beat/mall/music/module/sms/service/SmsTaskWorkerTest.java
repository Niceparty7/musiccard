package com.beat.mall.music.module.sms.service;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.common.entity.sms.SmsCrond;
import com.beat.mall.music.module.sms.config.SmsTaskProperties;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsTaskWorkerTest {

    @Test
    void shouldMarkTaskSuccessWhenAliyunReturnsOk() {
        BaseSmsService smsService = mock(BaseSmsService.class);
        SmsCrondService crondService = mock(SmsCrondService.class);
        when(smsService.sendTask(any(SmsCrond.class))).thenReturn(new SmsSendResultDTO().setOk(true));

        newWorker(smsService, crondService).process(task());

        verify(crondService).markSuccess(eq(1L), anyInt());
        verify(crondService, never()).markRetryWait(eq(1L), org.mockito.ArgumentMatchers.anyShort(), anyInt(), org.mockito.ArgumentMatchers.anyString(), anyInt());
    }

    @Test
    void shouldWaitForRetryWhenFailureIsRetryable() {
        BaseSmsService smsService = mock(BaseSmsService.class);
        SmsCrondService crondService = mock(SmsCrondService.class);
        when(smsService.sendTask(any(SmsCrond.class))).thenReturn(SmsSendResultDTO.fail("SEND_FAIL", "network timeout"));

        newWorker(smsService, crondService).process(task());

        verify(crondService).markRetryWait(eq(1L), eq((short) 1), anyInt(), org.mockito.ArgumentMatchers.contains("SEND_FAIL"), anyInt());
    }

    private SmsTaskWorker newWorker(BaseSmsService smsService, SmsCrondService crondService) {
        SmsTaskProperties properties = new SmsTaskProperties();
        properties.setMaxRetryCount(2);
        properties.setRetryDelaySeconds(60);
        return new SmsTaskWorker(smsService, crondService, properties);
    }

    private SmsCrond task() {
        return new SmsCrond().setId(1L).setPhone("13800000000").setContent("123456").setRetryCount((short) 0);
    }
}
