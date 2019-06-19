package config.impl;

import config.DatabaseConfig;
import me.exrates.adminservice.utils.LogUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(value = {"test"})
public class AdminDatabaseConfigImpl implements DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverClassname;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;


    @Override
    public String getUrl() {
        return this.dbUrl;
    }

    @Override
    public String getDriverClassName() {
        return this.dbDriverClassname;
    }

    @Override
    public String getUser() {
        return this.dbUsername;
    }

    @Override
    public String getPassword() {
        return this.dbPassword;
    }

}
