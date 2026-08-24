package com.beat.mall.music.module.api.console.controller;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.common.response.Response;
import com.beat.mall.music.module.auth.AuthService;
import com.beat.mall.music.module.sms.service.BaseSmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController("consoleSmsController")
@RequiredArgsConstructor
@RequestMapping(value = "/sms", headers = {"X-Client-Type=console", "X-Internal-Token"})
public class SmsController {
    private final BaseSmsService baseSmsService;
    private final AuthService providerAuthService;

    @RequestMapping(value = "/send-sync", headers = "X-Client-Type=console")
    public Response<SmsSendResultDTO> sendSync(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String phone) {
        providerAuthService.requireUser(userId);
        if (phone == null || phone.isBlank()) {
            return new Response<>(5002);
        }
        return new Response<>(1001, baseSmsService.sendSync(phone));
    }

    @RequestMapping(value = "/send-batch", headers = "X-Client-Type=console")
    public Response<List<SmsSendResultDTO>> sendBatch(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam String phones) {
        providerAuthService.requireUser(userId);
        if (phones == null || phones.isBlank()) {
            return new Response<>(5002);
        }
        List<String> phoneList = Arrays.asList(phones.split("[,;]"));
        if (phoneList.size() > 200) {
            return new Response<>(5002);
        }
        return new Response<>(1001, baseSmsService.sendBatch(phoneList));
    }

}



