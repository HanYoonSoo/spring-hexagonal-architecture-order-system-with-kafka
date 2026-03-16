package com.hanyoonsoo.ordersystem.adapter.config.kafka;

import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

@Configuration
public class KafkaConsumerFactoryConfig {

    @Bean
    public ConsumerFactory<String, OrderCreatedEvent> orderCreatedConsumerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = kafkaProperties.buildConsumerProperties();
        JsonDeserializer<OrderCreatedEvent> valueDeserializer = new JsonDeserializer<>(OrderCreatedEvent.class);
        return new DefaultKafkaConsumerFactory<>(properties, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
            @Qualifier("kafkaObjectTemplate") KafkaTemplate<String, Object> kafkaTemplate,
            KafkaConsumerErrorHandlerProperties properties
    ) {
        return new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, exception) -> new TopicPartition(
                        record.topic() + properties.getDltSuffix(),
                        properties.getDltPartition()
                )
        );
    }

    @Bean
    public DefaultErrorHandler kafkaDefaultErrorHandler(
            DeadLetterPublishingRecoverer deadLetterPublishingRecoverer,
            KafkaConsumerErrorHandlerProperties properties
    ) {
        FixedBackOff fixedBackOff = new FixedBackOff(
                properties.getRetryBackoffMillis(),
                properties.getRetryAttempts()
        );
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, fixedBackOff);
        errorHandler.setCommitRecovered(true);
        return errorHandler;
    }

    @Bean(name = "orderCreatedKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> orderCreatedKafkaListenerContainerFactory(
            ConsumerFactory<String, OrderCreatedEvent> orderCreatedConsumerFactory,
            DefaultErrorHandler kafkaDefaultErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderCreatedConsumerFactory);
        factory.setCommonErrorHandler(kafkaDefaultErrorHandler);
        return factory;
    }
}
