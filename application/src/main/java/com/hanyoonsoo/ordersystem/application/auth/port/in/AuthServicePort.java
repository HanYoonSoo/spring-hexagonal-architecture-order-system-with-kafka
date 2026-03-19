package com.hanyoonsoo.ordersystem.application.auth.port.in;

import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.dto.SignInCommand;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenResult;

public interface AuthServicePort {

    String REFRESH_TOKEN_COOKIE_NAME = "s_rt";

    TokenResult signIn(SignInCommand command);

    JwtUserClaims validateAndExtractUserClaimsFromAccessToken(String accessToken);

    TokenResult reissue(String accessToken, String refreshToken);

    void logout(String userId, String accessToken);
}
