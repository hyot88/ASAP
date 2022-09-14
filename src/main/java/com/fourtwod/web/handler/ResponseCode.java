package com.fourtwod.web.handler;

import lombok.Getter;

@Getter
public enum ResponseCode {

    // 공통
    COMM_S000(0, "OK"),
    COMM_E000(1000, "Internal Server Error"),
    COMM_E001(1001, "처리 중 에러가 발생하였습니다."),

    // 닉네임
    NICK_E001(2001, "2자 이상 10자 이하로 입력해주세요."),
    NICK_E002(2002, "한글,영문,숫자로 입력해주세요."),
    NICK_E003(2003, "중복된 닉네임입니다.");

    private int code;
    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
