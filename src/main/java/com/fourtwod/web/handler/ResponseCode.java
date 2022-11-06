package com.fourtwod.web.handler;

import lombok.Getter;

@Getter
public enum ResponseCode {

    // 공통
    COMM_S000(0, "OK"),
    COMM_E000(1000, "Internal Server Error"),
    COMM_E001(1001, "처리 중 에러가 발생하였습니다."),
    COMM_E002(1002, "파라미터가 잘못되었습니다."),

    // 닉네임
    NICK_E000(2000, "2자 이상 10자 이하로 입력해주세요."),
    NICK_E001(2001, "한글,영문,숫자로 입력해주세요."),
    NICK_E002(2002, "중복된 닉네임입니다."),

    // 미션
    MISS_E000(3000, "진행중인 미션이 있습니다."),
    MISS_E001(3001, "참여 가능한 시간이 아닙니다."),
    MISS_E002(3002, "변경한 이력이 없습니다.");

    private int code;
    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
