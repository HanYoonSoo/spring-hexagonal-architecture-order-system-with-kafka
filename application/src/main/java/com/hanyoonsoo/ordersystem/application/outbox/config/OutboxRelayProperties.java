package com.hanyoonsoo.ordersystem.application.outbox.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "outbox.relay")
public class OutboxRelayProperties {

    private boolean enabled = true;
    private int batchSize = 50;
    private long fixedDelayMillis = 500;
    private long retryBackoffMillis = 5000;
    private int maxRetryCount = 5;
}
