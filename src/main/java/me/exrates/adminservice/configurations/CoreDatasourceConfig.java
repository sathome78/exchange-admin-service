package me.exrates.adminservice.configurations;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
public class CoreDatasourceConfig {

    @Value("${spring.db-core.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.db-core.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.db-core.datasource.username}")
    private String user;
    @Value("${spring.db-core.ssm.password-path}")
    private String ssmPath;

    @Autowired
    private SSMGetter ssmGetter;

    @Bean(name = "coreDataSource")
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(ssmGetter.lookup(ssmPath));
        hikariConfig.setConnectionTimeout(30 * 1000);
        hikariConfig.setReadOnly(true);
        return new HikariDataSource(hikariConfig);
    }

    @DependsOn("coreDataSource")
    @Bean(name = "coreTemplate")
    public JdbcOperations jdbcTemplate(@Qualifier("coreDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @DependsOn("coreDataSource")
    @Bean(name = "coreNPTemplate")
    public NamedParameterJdbcOperations namedParameterJdbcTemplate(@Qualifier("coreDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "coreTxManager")
    public PlatformTransactionManager platformTransactionManager(@Qualifier("coreDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}