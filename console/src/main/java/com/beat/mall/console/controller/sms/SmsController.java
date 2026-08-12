package com.beat.mall.console.controller.sms;

import com.beat.mall.console.annotations.VerifiedUser;
import com.beat.mall.module.sms.domain.SmsSendResultDTO;
import com.beat.mall.module.sms.service.BaseSmsService;
import com.beat.mall.module.user.entity.User;
import com.beat.mall.utils.BaseUtil;
import com.beat.mall.utils.Response;
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
    public Response sendSync(@VerifiedUser User loginUser, @RequestParam String phone) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        if (phone == null || phone.isEmpty()) {
            return new Response(5002);
        }
        boolean success = true;
        SmsSendResultDTO r = null;
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
    public Response sendBatch(@VerifiedUser User loginUser, @RequestParam String phones) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
            return new Response(1002);
        }
        if (phones == null || phones.isEmpty()) {
            return new Response(5002);
        }
        boolean success = true;
        List<SmsSendResultDTO> results = null;
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
    public Response sendAsync(@VerifiedUser User loginUser, @RequestParam String phone) {
        if (BaseUtil.isEmpty(loginUser)) {
            log.warn("User not logged in.");
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