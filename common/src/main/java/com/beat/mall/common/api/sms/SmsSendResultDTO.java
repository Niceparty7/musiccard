package com.beat.mall.common.api.sms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsSendResultDTO {
    private boolean ok;
    private String verifyCode;
    private String bizId;
    private String requestId;
    private String code;
    private String message;
    private String errorCode;
    private String errorMessage;

    public static SmsSendResultDTO fail(String errorCode, String errorMessage) {
        return new SmsSendResultDTO()
                .setOk(false)
                .setErrorCode(errorCode)
                .setErrorMessage(errorMessage);
    }
}
