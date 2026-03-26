package com.hanyoonsoo.ordersystem.adapter.support.cleanup;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CleanUp {

    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;

    public CleanUp(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityManager = entityManager;
    }

    public void all() {
        all(true);
    }

    public void all(boolean disableForeignKeyConstraints) {
        List<String> tables = entityManager.getMetamodel().getEntities().stream()
                .map(entityType -> {
                    Table tableAnnotation = entityType.getJavaType().getAnnotation(Table.class);
                    return tableAnnotation != null ? tableAnnotation.name() : entityType.getName();
                })
                .toList();

        if (disableForeignKeyConstraints) {
            disableTriggers(tables);
        }

        try {
            for (String table : tables) {
                try {
                    jdbcTemplate.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY CASCADE");
                } catch (Exception ignored) {
                }
            }
        } finally {
            if (disableForeignKeyConstraints) {
                enableTriggers(tables);
            }
        }
    }

    private void disableTriggers(List<String> tables) {
        for (String table : tables) {
            try {
                jdbcTemplate.execute("ALTER TABLE " + table + " DISABLE TRIGGER ALL");
            } catch (Exception ignored) {
            }
        }
    }

    private void enableTriggers(List<String> tables) {
        for (String table : tables) {
            try {
                jdbcTemplate.execute("ALTER TABLE " + table + " ENABLE TRIGGER ALL");
            } catch (Exception ignored) {
            }
        }
    }
}
