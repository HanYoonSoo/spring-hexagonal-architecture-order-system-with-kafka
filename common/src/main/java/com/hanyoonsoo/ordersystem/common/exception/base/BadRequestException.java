package com.hanyoonsoo.ordersystem.common.exception.base;

import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;

public class BadRequestException extends GlobalException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
