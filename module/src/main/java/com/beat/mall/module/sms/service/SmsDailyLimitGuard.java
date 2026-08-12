package com.beat.mall.module.sms.service;

import com.beat.mall.module.sms.config.AliyunSmsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class SmsDailyLimitGuard {

    private final SmsLogService smsLogService;
    private final AliyunSmsProperties props;

    public synchronized boolean allowToday(String phone) {
        int dayStart = (int) (LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toEpochSecond());
        int dayEnd = dayStart + 24 * 60 * 60;
        Long sent = smsLogService.countByPhoneToday(phone, dayStart, dayEnd);
        return sent == null || sent < props.getDailyLimitPerPhone();
    }
}