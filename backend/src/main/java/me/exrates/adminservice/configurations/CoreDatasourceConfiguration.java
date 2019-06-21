package me.exrates.adminservice.configurations;

import lombok.extern.log4j.Log4j2;
import me.exrates.SSMGetter;
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

@Log4j2
@Configuration
@Order(2)
public class CoreDatasourceConfiguration extends DatabaseConfiguration {

    public final static String CORE_TEMPLATE = "coreTemplate";
    public final static String CORE_NP_TEMPLATE = "coreNPTemplate";

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
    @Bean(name = CORE_TEMPLATE)
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
