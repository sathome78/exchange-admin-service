package me.exrates.adminservice.configurations;

import me.exrates.SSMGetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@Order(1)
public class AdminDatasourceConfiguration extends DatabaseConfiguration {

    @Value("${db-admin.datasource.url}")
    private String databaseUrl;

    @Value("${db-admin.datasource.driver-class-name}")
    private String databaseDriverName;

    @Value("${db-admin.datasource.username}")
    private String databaseUsername;

    @Value("${db-admin.ssm.password-path}")
    private String ssmPath;

    @Autowired
    private SSMGetter ssmGetter;

    @Primary
    @Bean(name = "adminDataSource")
    public DataSource dataSource() {
        return createDataSource();
    }

    @Primary
    @DependsOn("adminDataSource")
    @Bean(name = "adminTemplate")
    public JdbcOperations jdbcTemplate(@Qualifier("adminDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Primary
    @DependsOn("adminDataSource")
    @Bean(name = "adminNPTemplate")
    public NamedParameterJdbcOperations adminTemplate(@Qualifier("adminDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Primary
    @DependsOn("adminDataSource")
    @Bean(name = "adminTxManager")
    public PlatformTransactionManager platformTransactionManager(@Qualifier("adminDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
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
        return ssmGetter.lookup(ssmPath);
    }

    @Override
    protected String getDatabaseDriverClassName() {
        return databaseDriverName;
    }
}