package com.beat.mall.module.api.app.controller;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.common.response.Response;
import com.beat.mall.module.auth.ProviderAuthService;
import com.beat.mall.module.sms.service.BaseSmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class AppSmsProviderController {
    private final BaseSmsService baseSmsService;
    private final ProviderAuthService providerAuthService;

    @RequestMapping(value = "/send-sync", headers = "X-Client-Type=app")
    public Response<SmsSendResultDTO> sendSync(
            @RequestParam String phone,
            @RequestHeader(value = "sign", required = false) String sign) {
        providerAuthService.requireSign(sign);
        if (phone == null || phone.isBlank()) {
            return new Response<>(5002);
        }
        return new Response<>(1001, baseSmsService.sendSync(phone));
    }

    @RequestMapping(value = "/send-batch", headers = "X-Client-Type=app")
    public Response<List<SmsSendResultDTO>> sendBatch(
            @RequestParam String phones,
            @RequestHeader(value = "sign", required = false) String sign) {
        providerAuthService.requireSign(sign);
        if (phones == null || phones.isBlank()) {
            return new Response<>(5002);
        }
        List<String> phoneList = Arrays.asList(phones.split("[,;]"));
        if (phoneList.size() > 200) {
            return new Response<>(5002);
        }
        return new Response<>(1001, baseSmsService.sendBatch(phoneList));
    }

    @RequestMapping(value = "/send-async", headers = "X-Client-Type=app")
    public Response<String> sendAsync(
            @RequestParam String phone,
            @RequestHeader(value = "sign", required = false) String sign) throws Exception {
        providerAuthService.requireSign(sign);
        if (phone == null || phone.isBlank()) {
            return new Response<>(5002);
        }
        return new Response<>(1001, "taskId:" + baseSmsService.submitAsyncTask(phone));
    }
}
