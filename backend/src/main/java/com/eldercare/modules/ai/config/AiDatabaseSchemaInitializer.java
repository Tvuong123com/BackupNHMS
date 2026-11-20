package com.eldercare.modules.ai.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 * Applies the additive handover migration and initializes fall-risk snapshots
 * on SQL Server application startup when running:
 * mvn spring-boot:run "-Dmaven.test.skip=true"
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AiDatabaseSchemaInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQLSERVER_CREATE_SNAPSHOT_TABLE = """
            IF OBJECT_ID(N'dbo.ai_fall_risk_snapshots', N'U') IS NULL
            BEGIN
                CREATE TABLE dbo.ai_fall_risk_snapshots (
                    id BIGINT IDENTITY(1,1) NOT NULL PRIMARY KEY,
                    resident_id BIGINT NOT NULL,
                    dfrs_score INT NOT NULL,
                    risk_level NVARCHAR(20) NOT NULL,
                    incident_subscore INT NOT NULL,
                    medication_subscore INT NOT NULL,
                    adl_subscore INT NOT NULL,
                    vital_subscore INT NOT NULL,
                    recent_falls_count INT NOT NULL,
                    active_frid_count INT NOT NULL,
                    method_version NVARCHAR(50) NULL,
                    assessed_at DATETIMEOFFSET(6) NOT NULL,
                    assessed_by NVARCHAR(255) NULL
                );
                CREATE INDEX IX_ai_fall_risk_snapshots_resident 
                    ON dbo.ai_fall_risk_snapshots(resident_id, assessed_at DESC);
            END;
            """;

    @Override
    public void run(ApplicationArguments args) {
        String product = jdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<String>)
                connection -> connection.getMetaData().getDatabaseProductName());
        if (product != null && product.contains("Microsoft SQL Server")) {
            var migration = new ResourceDatabasePopulator(new ClassPathResource("schema-migration-ai-handover.sql"));
            migration.setSeparator("GO");
            migration.execute(java.util.Objects.requireNonNull(jdbcTemplate.getDataSource()));
            var unicode = new ResourceDatabasePopulator(new ClassPathResource("schema-migration-care-plan-unicode.sql"));
            unicode.setSqlScriptEncoding("UTF-8");
            unicode.setSeparator("GO");
            unicode.execute(java.util.Objects.requireNonNull(jdbcTemplate.getDataSource()));
            log.info("Handover sharing schema verified.");
        }
        try {
            jdbcTemplate.execute(SQLSERVER_CREATE_SNAPSHOT_TABLE);
            log.info("Schema check completed: ai_fall_risk_snapshots table verified/initialized successfully.");
        } catch (Exception ex) {
            // Non-fatal: if running on H2 in tests or user DB has different DDL permissions, log and continue smoothly
            log.debug("Auto-schema initialization skipped or handled by migration script: {}", ex.getMessage());
        }
    }
}
