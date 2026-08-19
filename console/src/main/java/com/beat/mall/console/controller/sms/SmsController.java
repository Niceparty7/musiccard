package com.beat.mall.console.controller.sms;

import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.console.feign.SmsFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {
    private final SmsFeign smsFeign;

    @GetMapping("/send-sync")
    public Response<SmsSendResultDTO> sendSync(@VerifiedUser User loginUser,
                                               @RequestParam String phone) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : smsFeign.sendSync(loginUser.getId(), phone);
    }

    @GetMapping("/send-batch")
    public Response<List<SmsSendResultDTO>> sendBatch(@VerifiedUser User loginUser,
                                                      @RequestParam String phones) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : smsFeign.sendBatch(loginUser.getId(), phones);
    }

    @GetMapping("/send-async")
    public Response<String> sendAsync(@VerifiedUser User loginUser,
                                      @RequestParam String phone) {
        return BaseUtil.isEmpty(loginUser) ? new Response<>(1002)
                : smsFeign.sendAsync(loginUser.getId(), phone);
    }
}
