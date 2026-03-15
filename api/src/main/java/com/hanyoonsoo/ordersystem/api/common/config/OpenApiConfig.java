package com.hanyoonsoo.ordersystem.api.common.config;

import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    static {
        ModelResolver.enumsAsRef = true;
    }

    private static final DateTimeFormatter START_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    private static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final Environment environment;

    @Bean
    public OpenAPI openApi() {
        String hostUrl = environment.getProperty("server.host-url", "http://localhost:8080");
        String applicationName = environment.getProperty("spring.application.name", "order-system");

        return new OpenAPI()
                .components(
                        new Components().addSecuritySchemes(
                                "BearerToken",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(
                        new Info()
                                .title(applicationName)
                                .description("서버 실행 시간 - " + getApplicationStartDateTime())
                                .version("v1")
                )
                .servers(List.of(getServer(hostUrl)))
                .addSecurityItem(new SecurityRequirement().addList("BearerToken"));
    }

    @Bean
    public OpenApiCustomizer nullableCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) {
                return;
            }

            for (Schema<?> schema : openApi.getComponents().getSchemas().values()) {
                if (!"object".equals(schema.getType())) {
                    continue;
                }
                Map<String, Schema> properties = schema.getProperties();
                List<String> required = schema.getRequired();
                if (properties == null || required == null) {
                    continue;
                }

                for (Map.Entry<String, Schema> entry : properties.entrySet()) {
                    if (required.contains(entry.getKey())) {
                        continue;
                    }
                    entry.getValue().setNullable(true);
                    required.add(entry.getKey());
                }
            }
        };
    }

    private Server getServer(String hostUrl) {
        Server server = new Server().url(hostUrl);
        if (hasActiveProfile("dev")) {
            return server.description("개발 서버");
        }
        if (hasActiveProfile("prod")) {
            return server.description("프로덕션 서버");
        }
        return server.description("로컬 서버");
    }

    private boolean hasActiveProfile(String profile) {
        return Arrays.asList(environment.getActiveProfiles()).contains(profile);
    }

    private String getApplicationStartDateTime() {
        return ZonedDateTime.now(KST_ZONE_ID).format(START_TIME_FORMATTER);
    }
}
