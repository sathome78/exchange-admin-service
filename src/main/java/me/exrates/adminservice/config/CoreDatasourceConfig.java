package me.exrates.adminservice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.exrates.SSMGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

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

    @Value("${db-core.ssm.password-path}")
    private String ssmPath;

    @Autowired
    private SSMGetter ssmGetter;

    @Bean(name = "coreDataSource")
    public DataSource dataSource() {
        return createDataSource();
    }

    @Bean(name = "coreTemplate")
    public NamedParameterJdbcTemplate coreTemplate(@Qualifier("coreDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

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
