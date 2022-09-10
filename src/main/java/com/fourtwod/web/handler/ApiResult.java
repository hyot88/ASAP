package com.fourtwod.web.handler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResult<T> {

    private int code;
    private String message;
    private T data;

    public ApiResult(T data) {
        this.message = "OK";
        this.data = data;
    }

    public static ApiResult of() {
        return new ApiResult(ResponseCode.COMM_E000);
    }
}
