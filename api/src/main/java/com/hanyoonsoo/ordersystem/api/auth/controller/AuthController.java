package com.hanyoonsoo.ordersystem.api.auth.controller;

import com.hanyoonsoo.ordersystem.api.auth.dto.RefreshTokenReissueRequest;
import com.hanyoonsoo.ordersystem.api.auth.jwt.TokenDto;
import com.hanyoonsoo.ordersystem.api.auth.service.AuthTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthTokenService authTokenService;

    @PostMapping("/reissue")
    public TokenDto reissue(@Valid @RequestBody RefreshTokenReissueRequest request) {
        return authTokenService.reissueTokens(request.refreshToken());
    }
}
