package com.hanyoonsoo.ordersystem.adapter.support.container;

import com.hanyoonsoo.ordersystem.adapter.out.email.EmailSenderAdapter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
        basePackages = {
                "com.hanyoonsoo.ordersystem.adapter.config",
                "com.hanyoonsoo.ordersystem.adapter.out",
                "com.hanyoonsoo.ordersystem.adapter.support",
                "com.hanyoonsoo.ordersystem.common"
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = KafkaIntegrationTestApplication.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = EmailSenderAdapter.class
                )
        }
)
public class AdapterIntegrationTestApplication {
}
