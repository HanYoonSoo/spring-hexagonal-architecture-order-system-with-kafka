package com.hanyoonsoo.ordersystem.common.response;

import com.hanyoonsoo.ordersystem.common.exception.ErrorResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final ErrorResponse error;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> failure(ErrorResponse error) {
        return new ApiResponse<>(false, null, error);
    }
}
