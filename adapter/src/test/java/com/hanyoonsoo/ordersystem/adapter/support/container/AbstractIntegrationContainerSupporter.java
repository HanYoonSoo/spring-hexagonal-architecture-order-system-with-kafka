package com.hanyoonsoo.ordersystem.adapter.support.container;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

public abstract class AbstractIntegrationContainerSupporter {

    protected static GenericContainer<?> createRedisContainer(String image, int port) {
        GenericContainer<?> container = new GenericContainer<>(DockerImageName.parse(image))
                .withExposedPorts(port)
                .withReuse(true);
        container.start();
        return container;
    }

    protected static PostgreSQLContainer<?> createPostgresContainer(
            String image,
            String databaseName,
            String username,
            String password,
            int port
    ) {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse(image))
                .withDatabaseName(databaseName)
                .withUsername(username)
                .withPassword(password)
                .withExposedPorts(port)
                .withReuse(true);
        container.start();
        return container;
    }

    protected static KafkaContainer createKafkaContainer(String image) {
        KafkaContainer container = new KafkaContainer(DockerImageName.parse(image))
                .withReuse(true);
        container.start();
        return container;
    }

    protected static void registerRedisAndPostgresProperties(
            DynamicPropertyRegistry registry,
            GenericContainer<?> redisContainer,
            int redisPort,
            PostgreSQLContainer<?> postgresContainer,
            int postgresPort,
            String databaseName,
            String username,
            String password
    ) {
        registry.add("redis.host", redisContainer::getHost);
        registry.add("redis.port", () -> redisContainer.getMappedPort(redisPort));

        registry.add("spring.datasource.url", () -> "jdbc:postgresql://%s:%d/%s".formatted(
                postgresContainer.getHost(),
                postgresContainer.getMappedPort(postgresPort),
                databaseName
        ));
        registry.add("spring.datasource.username", () -> username);
        registry.add("spring.datasource.password", () -> password);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
    }

    protected static void registerKafkaProperties(DynamicPropertyRegistry registry, KafkaContainer kafkaContainer) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    protected static void createTopics(KafkaContainer kafkaContainer, List<NewTopic> topics) {
        try (AdminClient adminClient = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaContainer.getBootstrapServers()
        ))) {
            adminClient.createTopics(topics).all().get();
        } catch (Exception ignored) {
        }
    }
}
