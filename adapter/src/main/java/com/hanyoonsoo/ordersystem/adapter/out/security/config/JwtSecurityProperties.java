package com.hanyoonsoo.ordersystem.adapter.out.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.security.jwt")
public class JwtSecurityProperties {

    private String secret;
    private String issuer = "order-system";
    private long accessTokenExpirationSeconds = 3600;
    private long refreshTokenExpirationSeconds = 1209600;
}
