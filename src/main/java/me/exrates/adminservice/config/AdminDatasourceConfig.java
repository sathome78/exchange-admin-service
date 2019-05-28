package me.exrates.adminservice.config;

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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@Order(1)
public class AdminDatasourceConfig extends DBConfig {

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

    @Bean(name = "adminDataSource")
    public DataSource dataSource() {
        return createDataSource();
    }

    @DependsOn("adminDataSource")
    @Bean(name = "template")
    public JdbcOperations jdbcTemplate(@Qualifier("adminDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "adminTemplate")
    public NamedParameterJdbcTemplate adminTemplate(@Qualifier("adminDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Primary
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
