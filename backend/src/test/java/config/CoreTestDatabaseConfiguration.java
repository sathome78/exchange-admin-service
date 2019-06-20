package config;

import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

import static config.AbstractDatabaseContextTest.TEST_CORE_DATASOURCE;
import static config.AbstractDatabaseContextTest.TEST_CORE_NP_TEMPLATE;
import static config.AbstractDatabaseContextTest.TEST_CORE_TEMPLATE;

@Configuration
@Profile("test")
public class CoreTestDatabaseConfiguration {

    @Value("${db-core.datasource.url}")
    private String dbUrl;

    @Value("${db-core.datasource.driver-class-name}")
    private String dbDriverClassname;

    @Value("${db-core.datasource.username}")
    private String dbUsername;

    @Value("${db-core.datasource.password}")
    private String dbPassword;

    @Bean(name = TEST_CORE_DATASOURCE)
    public DataSource coreDataSource() {
        final HikariDataSource hikariDataSource =
                HikariDataSourceFactory.createCoreDataSource();

        Flyway flyway = Flyway.configure()
                .dataSource(hikariDataSource)
                .baselineOnMigrate(true)
                .locations("db/data/core/")
                .load();
        flyway.migrate();

        return hikariDataSource;
    }

    @Bean(name = TEST_CORE_NP_TEMPLATE)
    public NamedParameterJdbcOperations coreNPTemplate(@Qualifier(TEST_CORE_DATASOURCE) DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = TEST_CORE_TEMPLATE)
    public JdbcOperations coreTemplate(@Qualifier(TEST_CORE_DATASOURCE) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}