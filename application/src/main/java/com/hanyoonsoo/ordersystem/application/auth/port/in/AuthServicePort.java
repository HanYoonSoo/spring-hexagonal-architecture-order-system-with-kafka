package com.hanyoonsoo.ordersystem.application.auth.port.in;

import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.dto.SignInCommand;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenDto;

public interface AuthServicePort {

    String REFRESH_TOKEN_COOKIE_NAME = "s_rt";

    TokenDto signIn(SignInCommand command);

    JwtUserClaims validateAndExtractUserClaimsFromAccessToken(String accessToken);

    TokenDto reissue(String accessToken, String refreshToken);

    void logout(String userId, String accessToken);
}
