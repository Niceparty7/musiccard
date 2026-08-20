package com.beat.mall.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private ResponseStatus status;
    private T result;

    public Response(int code) {
        this.status = new ResponseStatus()
                .setCode(code)
                .setMsg(ResponseCode.getMsg(code));
    }

    public Response(int code, T result) {
        this.status = new ResponseStatus()
                .setCode(code)
                .setMsg(ResponseCode.getMsg(code));
        this.result = result;
    }
}
