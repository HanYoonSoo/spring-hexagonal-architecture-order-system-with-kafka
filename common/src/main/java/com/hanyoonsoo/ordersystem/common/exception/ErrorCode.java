package com.hanyoonsoo.ordersystem.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_REQUEST(400, "A001", "잘못된 요청입니다."),
    INVALID_CREDENTIAL_FORMAT(400, "A002", "인증정보 형식이 올바르지 않습니다."),
    EMAIL_FORMAT_ERROR(400, "A003", "이메일 형식이 아닙니다."),
    PASSWORD_LENGTH_ERROR(400, "A004", "비밀번호는 8자 이상 30자 이하여야 합니다."),
    DUPLICATE_USER_CREDENTIAL(409, "A005", "이미 존재하는 사용자 인증정보입니다."),
    EMAIL_OR_PASSWORD_MISMATCH(401, "A006", "이메일 또는 비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(401, "A007", "유효하지 않은 토큰입니다."),
    ACCESS_TOKEN_EXPIRED(401, "A008", "액세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(401, "A009", "리프레시 토큰이 만료되었습니다."),
    AUTHENTICATION_FAILED(401, "A010", "유효하지 않은 접근입니다."),
    ROLE_MISMATCH(401, "A011", "토큰의 권한 정보가 현재 권한과 일치하지 않습니다."),
    REFRESH_TOKEN_MISMATCH(401, "A012", "리프레시 토큰이 일치하지 않습니다."),
    UNAUTHORIZED_ORIGIN(401, "A014", "허용되지 않은 출처입니다."),
    LOGGED_OUT_ACCESS_TOKEN(401, "A015", "로그아웃된 액세스 토큰입니다."),
    USER_NOT_FOUND(404, "A016", "사용자를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(404, "A017", "상품을 찾을 수 없습니다."),
    ORDER_NOT_FOUND(404, "A018", "주문을 찾을 수 없습니다."),
    OUT_OF_STOCK(409, "A019", "재고가 부족합니다."),
    LOCK_ACQUISITION_FAILED(409, "A020", "잠금 획득에 실패했습니다."),
    DUPLICATE_IDEMPOTENCY_REQUEST(409, "A022", "이미 처리 중이거나 처리된 멱등 요청입니다."),
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
