package com.hanyoonsoo.ordersystem.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final OffsetDateTime timestamp;
    private final String code;
    private final String message;
    private final String path;

    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(OffsetDateTime.now(), code, message, path);
    }
}
