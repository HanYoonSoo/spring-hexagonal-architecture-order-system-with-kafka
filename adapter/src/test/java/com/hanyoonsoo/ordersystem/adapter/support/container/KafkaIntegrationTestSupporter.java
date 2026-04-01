package com.hanyoonsoo.ordersystem.adapter.support.container;

import com.hanyoonsoo.ordersystem.application.email.port.in.EmailServicePort;
import com.hanyoonsoo.ordersystem.application.notification.port.in.NotificationServicePort;
import com.hanyoonsoo.ordersystem.application.order.port.in.InventoryServicePort;
import com.hanyoonsoo.ordersystem.application.order.port.in.OrderServicePort;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import java.util.List;

@Tag("integration")
@SpringBootTest(classes = KafkaIntegrationTestApplication.class)
@ActiveProfiles("test")
@Testcontainers
public abstract class KafkaIntegrationTestSupporter extends AbstractIntegrationContainerSupporter {

    private static final String KAFKA_IMAGE = "apache/kafka:3.9.1";
    private static final String ORDER_CREATED_TOPIC = "order.created.v1";
    private static final String ORDER_RESULT_TOPIC = "order.result.v1";
    private static final String EMAIL_SEND_REQUESTED_TOPIC = "email.send.requested.v1";
    private static final String ORDER_CREATED_DLT_TOPIC = "order.created.v1.dlt";

    static final KafkaContainer KAFKA_CONTAINER = createKafkaContainer(KAFKA_IMAGE);

    static {
        createTopics(KAFKA_CONTAINER, List.of(
                new NewTopic(ORDER_CREATED_TOPIC, 1, (short) 1),
                new NewTopic(ORDER_RESULT_TOPIC, 1, (short) 1),
                new NewTopic(EMAIL_SEND_REQUESTED_TOPIC, 1, (short) 1),
                new NewTopic(ORDER_CREATED_DLT_TOPIC, 1, (short) 1)
        ));
    }

    @MockitoBean
    protected InventoryServicePort inventoryService;

    @MockitoBean
    protected OrderServicePort orderService;

    @MockitoBean
    protected NotificationServicePort notificationService;

    @MockitoBean
    protected EmailServicePort emailService;

    @Autowired
    protected KafkaTemplate<String, Object> kafkaObjectTemplate;

    @AfterEach
    void resetMocks() {
        Mockito.reset(inventoryService, orderService, notificationService, emailService);
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registerKafkaProperties(registry, KAFKA_CONTAINER);
    }

    protected String kafkaBootstrapServers() {
        return KAFKA_CONTAINER.getBootstrapServers();
    }
}
