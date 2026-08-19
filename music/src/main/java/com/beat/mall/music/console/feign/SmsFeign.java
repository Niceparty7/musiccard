package com.beat.mall.music.console.feign;

import com.beat.mall.music.console.config.ConsoleFeignConfiguration;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "music", contextId = "consoleSmsFeign", configuration = ConsoleFeignConfiguration.class)
public interface SmsFeign {
    @GetMapping("/sms/send-sync")
    Response<SmsSendResultDTO> sendSync(@RequestHeader("X-User-Id") Long userId,
                                        @RequestParam("phone") String phone);

    @GetMapping("/sms/send-batch")
    Response<List<SmsSendResultDTO>> sendBatch(@RequestHeader("X-User-Id") Long userId,
                                               @RequestParam("phones") String phones);

    @GetMapping("/sms/send-async")
    Response<String> sendAsync(@RequestHeader("X-User-Id") Long userId,
                               @RequestParam("phone") String phone);
}


