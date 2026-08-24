package com.beat.mall.music.module.sms.service;

import com.beat.mall.music.module.sms.config.AliyunSmsProperties;
import com.beat.mall.music.module.sms.mapper.SmsForbidMapper;
import com.beat.mall.music.module.sms.mapper.SmsUseMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SmsSendGuardServiceTest {

    private SmsUseMapper smsUseMapper;
    private SmsForbidMapper smsForbidMapper;
    private SmsSendGuardService smsSendGuardService;

    @BeforeEach
    void setUp() {
        smsUseMapper = mock(SmsUseMapper.class);
        smsForbidMapper = mock(SmsForbidMapper.class);
        AliyunSmsProperties properties = new AliyunSmsProperties();
        properties.setMinuteLimitPerPhone(3);
        properties.setForbidSeconds(3600);
        smsSendGuardService = new SmsSendGuardService(smsUseMapper, smsForbidMapper, properties);
    }

    @Test
    void shouldRejectPhoneThatIsAlreadyForbidden() {
        String phone = "13800000000";
        when(smsForbidMapper.countActive(eq(phone), anyInt())).thenReturn(1);

        assertFalse(smsSendGuardService.tryAcquire(phone));
        verify(smsUseMapper, never()).increment(eq(phone), anyInt(), anyInt());
    }

    @Test
    void shouldAllowRequestBeforeMinuteLimitIsReached() {
        String phone = "13800000000";
        when(smsForbidMapper.countActive(eq(phone), anyInt())).thenReturn(0);
        when(smsUseMapper.selectRequestCount(eq(phone), anyInt())).thenReturn(2);

        assertTrue(smsSendGuardService.tryAcquire(phone));
        verify(smsUseMapper).increment(eq(phone), anyInt(), anyInt());
        verify(smsForbidMapper, never()).insertOrExtend(
                eq(phone), anyInt(), anyInt(), contains("一分钟"), anyInt());
    }

    @Test
    void shouldForbidThirdRequestForOneHour() {
        String phone = "13800000000";
        when(smsForbidMapper.countActive(eq(phone), anyInt())).thenReturn(0);
        when(smsUseMapper.selectRequestCount(eq(phone), anyInt())).thenReturn(3);

        assertFalse(smsSendGuardService.tryAcquire(phone));
        verify(smsForbidMapper).insertOrExtend(
                eq(phone), anyInt(), anyInt(), contains("一分钟"), anyInt());
    }
}
