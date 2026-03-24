package com.hanyoonsoo.ordersystem.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final String code;
    private final String message;
    private final String path;

    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), code, message, path);
    }
}
