package com.fourtwod.web.handler;

import lombok.Getter;

@Getter
public enum ResponseCode {

    COMM_S000(0, "OK"),
    COMM_E000(1000, "Internal Server Error"),
    COMM_E001(1001, "처리 중 에러가 발생하였습니다."),
    COMM_E002(1002, "닉네임이 중복됩니다.");

    private int code;
    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
