package com.hanyoonsoo.ordersystem.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_REQUEST(400, "A001", "잘못된 요청입니다."),
    INVALID_TOKEN(401, "A007", "유효하지 않은 토큰입니다."),
    ACCESS_TOKEN_EXPIRED(401, "A008", "액세스 토큰이 만료되었습니다."),
    AUTHENTICATION_FAILED(401, "A010", "유효하지 않은 접근입니다."),
    FORBIDDEN_USER(403, "A013", "권한이 없는 유저입니다."),
    INTERNAL_SERVER_ERROR(500, "A021", "서버 내부 에러입니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public String message(String detail) {
        if (detail == null || detail.isBlank()) {
            return message;
        }
        return message + " (" + detail + ")";
    }
}
