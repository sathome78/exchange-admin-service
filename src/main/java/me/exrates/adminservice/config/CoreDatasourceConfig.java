package me.exrates.adminservice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@Configuration
@Order(2)
public class CoreDatasourceConfig extends DBConfig {

    private static final Logger logger = LoggerFactory.getLogger(CoreDatasourceConfig.class);

    @Value("${db-core.datasource.url}")
    private String databaseUrl;

    @Value("${db-core.datasource.driver-class-name}")
    private String databaseDriverName;

    @Value("${db-core.datasource.username}")
    private String databaseUsername;

    @Value("${db-core.datasource.password}")
    private String databasePassword;

    @Bean(name = "coreDataSource")
    public DataSource dataSource() {
        return createDataSource();
    }

    @Bean(name = "coreTemplate")
    public NamedParameterJdbcTemplate coreTemplate(@Qualifier("coreDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    protected String getDatabaseUrl() {
        return databaseUrl;
    }

    @Override
    protected String getDatabaseUsername() {
        return databaseUsername;
    }

    @Override
    protected String getDatabasePassword() {
        return databasePassword;
    }

    @Override
    protected String getDatabaseDriverClassName() {
        return databaseDriverName;
    }
}
