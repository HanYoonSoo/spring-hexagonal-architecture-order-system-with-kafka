package com.hanyoonsoo.ordersystem.common.exception.base;

import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;

public class NotFoundException extends GlobalException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
