package com.hanyoonsoo.ordersystem.api.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cookie")
public class CookieProperties {

    private String sameSite = "Lax";
    private boolean secure = false;
}
