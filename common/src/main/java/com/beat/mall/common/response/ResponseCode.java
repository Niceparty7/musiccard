package com.beat.mall.common.response;

import java.util.HashMap;
import java.util.Map;

public final class ResponseCode {
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();

    static {
        STATUS_MAP.put(1001, "OK");
        STATUS_MAP.put(1002, "没有登录哦~");
        STATUS_MAP.put(1010, "账号密码不匹配或账号不存在");
        STATUS_MAP.put(2014, "账号尚未注册");
        STATUS_MAP.put(3051, "产品必填信息不能为空");
        STATUS_MAP.put(3052, "产品ID不正确");
        STATUS_MAP.put(4003, "没有权限");
        STATUS_MAP.put(4004, "链接超时");
        STATUS_MAP.put(4005, "操作失败");
        STATUS_MAP.put(4006, "上传失败");
        STATUS_MAP.put(4007, "下载失败");
        STATUS_MAP.put(4008, "数据不存在");
        STATUS_MAP.put(4009, "WP参数不合法");
        STATUS_MAP.put(5001, "短信同号当日已达上限");
        STATUS_MAP.put(5002, "短信参数缺失或非法");
        STATUS_MAP.put(5003, "短信发送失败");
        STATUS_MAP.put(5004, "短信提交任务失败");
    }

    private ResponseCode() {
    }

    public static String getMsg(Integer code) {
        return STATUS_MAP.get(code);
    }
}
