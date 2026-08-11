package com.beat.mall.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseCode {
    private static final Map<Integer, String> statusMap = new HashMap<Integer, String>();

    static {
        statusMap.put(1001, "OK");
        statusMap.put(1002, "没有登录哦~");
        statusMap.put(1010, "账号密码不匹配或账号不存在");

        //create user and forget password
        statusMap.put(2014, "账号尚未注册");

        //console error
        //product error
        statusMap.put(3051, "产品必填信息不能为空");
        statusMap.put(3052, "产品ID不正确");

        statusMap.put(4003, "没有权限");
        statusMap.put(4004, "链接超时");
        //business error
        statusMap.put(4005, "操作失败");
        statusMap.put(4006, "上传失败");
        statusMap.put(4007, "下载失败");
        statusMap.put(4008, "数据不存在");

        //sms error
        statusMap.put(5001, "短信同号当日已达上限");
        statusMap.put(5002, "短信参数缺失或非法");
        statusMap.put(5003, "短信发送失败");
        statusMap.put(5004, "短信提交任务失败");
    }

    public static String getMsg(Integer code) {
        return statusMap.get(code);
    }
}