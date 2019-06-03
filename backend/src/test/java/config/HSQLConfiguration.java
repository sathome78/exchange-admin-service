package config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class HSQLConfiguration {

    @Autowired
    private Environment env;

    @Bean("testInMemoDataSource")
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("db-core.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("db-core.datasource.url"));
        dataSource.setUsername(env.getProperty("db-core.datasource.username"));
        dataSource.setPassword(env.getProperty("db-core.datasource.password"));
        return dataSource;
    }

    @DependsOn("testInMemoDataSource")
    @Bean("testInMemoJdbcTemplate")
    public NamedParameterJdbcOperations namedParameterJdbcOperations(@Qualifier("testInMemoDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
