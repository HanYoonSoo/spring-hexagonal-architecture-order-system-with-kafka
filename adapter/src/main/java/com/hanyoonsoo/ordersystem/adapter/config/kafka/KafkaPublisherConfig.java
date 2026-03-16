package com.hanyoonsoo.ordersystem.adapter.config.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaPublisherConfig {

    @Bean("kafkaStringProducerFactory")
    public ProducerFactory<String, String> kafkaStringProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = kafkaProperties.buildProducerProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean("kafkaStringTemplate")
    public KafkaTemplate<String, String> kafkaStringTemplate(
            @Qualifier("kafkaStringProducerFactory") ProducerFactory<String, String> kafkaStringProducerFactory
    ) {
        return new KafkaTemplate<>(kafkaStringProducerFactory);
    }

    @Bean("kafkaObjectProducerFactory")
    public ProducerFactory<String, Object> kafkaObjectProducerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> properties = kafkaProperties.buildProducerProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        properties.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean("kafkaObjectTemplate")
    public KafkaTemplate<String, Object> kafkaObjectTemplate(
            @Qualifier("kafkaObjectProducerFactory") ProducerFactory<String, Object> kafkaObjectProducerFactory
    ) {
        return new KafkaTemplate<>(kafkaObjectProducerFactory);
    }
}
