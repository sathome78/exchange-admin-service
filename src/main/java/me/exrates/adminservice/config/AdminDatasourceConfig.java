package me.exrates.adminservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Order(2)
public class AdminDatasourceConfig extends DBConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String databaseDriverName;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Bean(name = "adminDataSource")
    public DataSource dataSource() {
        return createDataSource();
    }

    @Bean(name = "adminTemplate")
    public NamedParameterJdbcTemplate adminTemplate(@Qualifier("adminDataSource") DataSource dataSource) {
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
