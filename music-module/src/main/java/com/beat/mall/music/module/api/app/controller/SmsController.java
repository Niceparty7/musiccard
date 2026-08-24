package com.beat.mall.music.module.api.app.controller;

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

@RestController("appSmsController")
@RequiredArgsConstructor
@RequestMapping(value = "/sms", headers = {"X-Client-Type=app", "X-Internal-Token"})
public class SmsController {
    private final BaseSmsService baseSmsService;
    private final AuthService authService;

    @RequestMapping(value = "/send-sync", headers = "X-Client-Type=app")
    public Response<SmsSendResultDTO> sendSync(
            @RequestParam String phone,
            @RequestHeader(value = "sign", required = false) String sign) {
        authService.requireSign(sign);
        if (phone == null || phone.isBlank()) {
            return new Response<>(5002);
        }
        return new Response<>(1001, baseSmsService.sendSync(phone));
    }

    @RequestMapping(value = "/send-batch", headers = "X-Client-Type=app")
    public Response<List<SmsSendResultDTO>> sendBatch(
            @RequestParam String phones,
            @RequestHeader(value = "sign", required = false) String sign) {
        authService.requireSign(sign);
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



