package com.beat.mall.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ResponseStatus {
    private Integer code;
    private String msg;
}
