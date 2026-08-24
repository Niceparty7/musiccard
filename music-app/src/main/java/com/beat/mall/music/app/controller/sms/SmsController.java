package com.beat.mall.music.app.controller.sms;

import com.beat.mall.music.app.annotations.VerifiedUser;
import com.beat.mall.music.app.feign.SmsFeign;
import com.beat.mall.common.api.sms.SmsSendResultDTO;
import com.beat.mall.common.api.sms.SmsTaskSubmitResultDTO;
import com.beat.mall.common.entity.user.User;
import com.beat.mall.common.response.Response;
import com.beat.mall.common.utils.BaseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("appSmsController")
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {
    private final SmsFeign smsFeign;

    @GetMapping("/send-sync")
    public Response<SmsSendResultDTO> sendSync(@VerifiedUser User loginUser,
                                               @RequestParam String phone,
                                               HttpServletRequest request) {
        if (BaseUtil.isEmpty(loginUser)) {
            return new Response<>(1002);
        }
        return smsFeign.sendSync(phone, getSign(request));
    }

    @GetMapping("/send-batch")
    public Response<List<SmsSendResultDTO>> sendBatch(@VerifiedUser User loginUser,
                                                      @RequestParam String phones,
                                                      HttpServletRequest request) {
        if (BaseUtil.isEmpty(loginUser)) {
            return new Response<>(1002);
        }
        return smsFeign.sendBatch(phones, getSign(request));
    }

    @GetMapping("/send-async")
    public Response<SmsTaskSubmitResultDTO> sendAsync(@VerifiedUser User loginUser,
                                                       @RequestParam String phone,
                                                       HttpServletRequest request) {
        if (BaseUtil.isEmpty(loginUser)) {
            return new Response<>(1002);
        }
        return smsFeign.sendAsync(phone, getSign(request));
    }

    private String getSign(HttpServletRequest request) {
        String sign = request.getHeader("sign");
        return BaseUtil.isEmpty(sign) ? request.getParameter("sign") : sign;
    }
}


