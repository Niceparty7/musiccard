package com.beat.mall.common.api.sms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmsTaskSubmitResultDTO {
    private boolean accepted;
    private Long taskId;
    private String status;
    private String errorCode;
    private String errorMessage;

    public static SmsTaskSubmitResultDTO rejected(String errorCode, String errorMessage) {
        return new SmsTaskSubmitResultDTO()
                .setAccepted(false)
                .setErrorCode(errorCode)
                .setErrorMessage(errorMessage);
    }
}
