package com.hanyoonsoo.ordersystem.common.exception.base;

import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    protected GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected GlobalException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public int status() {
        return errorCode.getStatus();
    }

    public String code() {
        return errorCode.getCode();
    }
}
