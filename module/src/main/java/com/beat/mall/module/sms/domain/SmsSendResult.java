package com.beat.mall.module.sms.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsSendResult {
    private boolean ok;
    private String verifyCode;   // 后台生成的验证码（前端无需传入，发送后返回给调用方）
    private String bizId;
    private String requestId;
    private String code;
    private String message;
    private String errorCode;
    private String errorMessage;

    public static SmsSendResult fail(String errorCode, String errorMessage) {
        return new SmsSendResult()
                .setOk(false)
                .setErrorCode(errorCode)
                .setErrorMessage(errorMessage);
    }
}
