package me.exrates.adminservice.configurations;

import me.exrates.SSMGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@Order(2)
public class CoreDatasourceConfiguration extends DatabaseConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CoreDatasourceConfiguration.class);

    @Value("${db-core.datasource.url}")
    private String databaseUrl;

    @Value("${db-core.datasource.driver-class-name}")
    private String databaseDriverName;

    @Value("${db-core.datasource.username}")
    private String databaseUsername;

    @Value("${db-core.ssm.password-path}")
    private String ssmPath;

    @Autowired
    private SSMGetter ssmGetter;

    @Bean(name = "coreDataSource")
    public DataSource dataSource() {
        return createDataSource();
    }

    @DependsOn("coreDataSource")
    @Bean(name = "coreTemplate")
    public JdbcOperations jdbcTemplate(@Qualifier("coreDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @DependsOn("coreDataSource")
    @Bean(name = "coreNPTemplate")
    public NamedParameterJdbcOperations coreTemplate(@Qualifier("coreDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @DependsOn("coreDataSource")
    @Bean(name = "coreTxManager")
    public PlatformTransactionManager platformTransactionManager(@Qualifier("coreDataSource") DataSource dataSource) {
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