package com.hanyoonsoo.ordersystem.adapter.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka.consumer.error-handler")
public class KafkaConsumerErrorHandlerProperties {

    private long retryAttempts = 3;
    private long retryBackoffMillis = 1000;
    private String dltSuffix = ".dlt";
    private int dltPartition = 0;
}
