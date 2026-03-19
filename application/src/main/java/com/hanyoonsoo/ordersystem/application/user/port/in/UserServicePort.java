package com.hanyoonsoo.ordersystem.application.user.port.in;

import com.hanyoonsoo.ordersystem.application.user.dto.SignUpCommand;
import com.hanyoonsoo.ordersystem.application.user.dto.UserDetailResult;

import java.util.UUID;

public interface UserServicePort {
    void signUp(SignUpCommand command);

    UserDetailResult getMyInfo(UUID userId);
}
