package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing
@EntityScan(basePackages = "com.hanyoonsoo.ordersystem.core.domain")
@EnableJpaRepositories(basePackages = "com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa")
public class JpaConfig {
}
