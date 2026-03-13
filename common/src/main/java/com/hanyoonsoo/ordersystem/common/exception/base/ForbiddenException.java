package com.hanyoonsoo.ordersystem.common.exception.base;

import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;

public class ForbiddenException extends GlobalException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ForbiddenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
