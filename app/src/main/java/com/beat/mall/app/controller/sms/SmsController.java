package com.beat.mall.app.controller.sms;

import com.beat.mall.module.sms.domain.SmsSendResult;
import com.beat.mall.module.sms.service.BaseSmsService;
import com.beat.mall.utils.Response;
import com.beat.mall.utils.SignUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final BaseSmsService baseSmsService;

    @RequestMapping("/send-sync")
    public Response sendSync(@RequestParam String phone, HttpServletRequest request) {
        String token = request.getHeader("sign");
        if (token == null) {
            log.error("token未获取，未登录");
            return new Response(1002);
        }
        try {
            SignUtil.parseSign(token);
        } catch (Exception e) {
            log.error("token解析失败，未登录");
            return new Response(1002);
        }
        if (phone == null || phone.isEmpty()) {
            return new Response(5002);
        }
        boolean success = true;
        SmsSendResult r = null;
        try {
            r = baseSmsService.sendSync(phone);
        } catch (Exception e) {
            success = false;
            log.error("send sync fail, phone:{}", phone, e);
        }
        if (!success) {
            return new Response(5003);
        }
        return new Response(1001, r);
    }

    @RequestMapping("/send-batch")
    public Response sendBatch(@RequestParam String phones, HttpServletRequest request) {
        String token = request.getHeader("sign");
        if (token == null) {
            log.error("token未获取，未登录");
            return new Response(1002);
        }
        try {
            SignUtil.parseSign(token);
        } catch (Exception e) {
            log.error("token解析失败，未登录");
            return new Response(1002);
        }
        if (phones == null || phones.isEmpty()) {
            return new Response(5002);
        }
        boolean success = true;
        List<SmsSendResult> results = null;
        try {
            List<String> phoneList = Arrays.asList(phones.split("[,;]"));
            if (phoneList.size() > 200) {
                return new Response(5002);
            }
            results = baseSmsService.sendBatch(phoneList);
        } catch (Exception e) {
            success = false;
            log.error("send batch fail, phones:{}", phones, e);
        }
        if (!success) {
            return new Response(5003);
        }
        return new Response(1001, results);
    }

    @RequestMapping("/send-async")
    public Response sendAsync(@RequestParam String phone, HttpServletRequest request) {
        String token = request.getHeader("sign");
        if (token == null) {
            log.error("token未获取，未登录");
            return new Response(1002);
        }
        try {
            SignUtil.parseSign(token);
        } catch (Exception e) {
            log.error("token解析失败，未登录");
            return new Response(1002);
        }
        if (phone == null || phone.isEmpty()) {
            return new Response(5002);
        }
        boolean success = true;
        Long taskId = null;
        try {
            taskId = baseSmsService.submitAsyncTask(phone);
        } catch (Exception e) {
            success = false;
            log.error("submit async fail, phone:{}", phone, e);
        }
        if (!success) {
            return new Response(5004);
        }
        return new Response(1001, "taskId:" + taskId);
    }
}