package uk.gov.hmcts.reform.amapi;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class FlywayMigrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayMigrator.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.flyway.locations}")
    private String[] locations;

    @Value("${spring.flyway.enabled}")
    private boolean enabled;

    public void runMigrations() {
        if (enabled) {
            LOGGER.info("Migrations starting...");
            Flyway.configure()
                    .dataSource(dbUrl, dbUsername, dbPassword)
                    .locations(locations)
                    .load()
                    .migrate();
        }
    }
}
