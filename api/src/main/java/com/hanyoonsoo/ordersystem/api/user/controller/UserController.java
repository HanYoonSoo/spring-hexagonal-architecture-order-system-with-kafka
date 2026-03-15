package com.hanyoonsoo.ordersystem.api.user.controller;

import com.hanyoonsoo.ordersystem.api.auth.utils.SecurityUtils;
import com.hanyoonsoo.ordersystem.api.user.dto.request.SignUpRequest;
import com.hanyoonsoo.ordersystem.api.user.dto.response.UserInfoResponse;
import com.hanyoonsoo.ordersystem.application.user.port.in.UserServicePort;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServicePort userService;

    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> me() {
        UserInfoResponse response = UserInfoResponse.from(
                userService.getMyInfo(SecurityUtils.requiredAuthenticatedUserId())
        );
        return ApiResponse.success(response);
    }

    @PostMapping("")
    public ApiResponse<Void> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        userService.signUp(request.toCommand());

        return ApiResponse.success(null);
    }
}
