package com.beat.mall.app.feign;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.common.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "module", contextId = "appSmsFeign")
public interface AppSmsFeign {
    @GetMapping("/sms/send-sync")
    Response<SmsSendResultDTO> sendSync(@RequestParam("phone") String phone,
                                        @RequestHeader("sign") String sign);

    @GetMapping("/sms/send-batch")
    Response<List<SmsSendResultDTO>> sendBatch(@RequestParam("phones") String phones,
                                               @RequestHeader("sign") String sign);

    @GetMapping("/sms/send-async")
    Response<String> sendAsync(@RequestParam("phone") String phone,
                               @RequestHeader("sign") String sign);
}
