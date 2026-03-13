package com.hanyoonsoo.ordersystem.common.exception.base;

import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;

public class ConflictException extends GlobalException {

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
