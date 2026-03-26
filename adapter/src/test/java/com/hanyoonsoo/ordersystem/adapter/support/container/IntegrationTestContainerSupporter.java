package com.hanyoonsoo.ordersystem.adapter.support.container;

import com.hanyoonsoo.ordersystem.adapter.support.cleanup.CleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tag("integration")
@SpringBootTest(classes = AdapterIntegrationTestApplication.class)
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrationTestContainerSupporter extends AbstractIntegrationContainerSupporter {

    private static final String REDIS_IMAGE = "redis:7.2-alpine";
    private static final int REDIS_PORT = 6379;
    private static final String POSTGRES_IMAGE = "postgres:17";
    private static final int POSTGRES_PORT = 5432;
    private static final String POSTGRES_DB = "order_system_test";
    private static final String POSTGRES_USER = "test";
    private static final String POSTGRES_PASSWORD = "test";

    static final GenericContainer<?> REDIS_CONTAINER = createRedisContainer(REDIS_IMAGE, REDIS_PORT);

    static final PostgreSQLContainer<?> POSTGRES_CONTAINER = createPostgresContainer(
            POSTGRES_IMAGE,
            POSTGRES_DB,
            POSTGRES_USER,
            POSTGRES_PASSWORD,
            POSTGRES_PORT
    );

    @Autowired
    private CleanUp cleanUp;

    @Autowired
    protected RedisTemplate<String, String> redisTemplate;

    @AfterEach
    void cleanUpEnvironment() {
        cleanUp.all(true);
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registerRedisAndPostgresProperties(
                registry,
                REDIS_CONTAINER,
                REDIS_PORT,
                POSTGRES_CONTAINER,
                POSTGRES_PORT,
                POSTGRES_DB,
                POSTGRES_USER,
                POSTGRES_PASSWORD
        );
    }
}
