package config.impl;

import config.DatabaseConfig;
import me.exrates.adminservice.utils.LogUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(value = {"test"})
public class CoreDatabaseConfigImpl implements DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverClassname;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.scheme-name}")
    private String rootSchemeName;

    private final String dbScheme;

    public CoreDatabaseConfigImpl(String schemaName) {
        this.dbScheme = CORE_PREFIX + schemaName;
    }

    public CoreDatabaseConfigImpl(DatabaseConfig config) {
        this.dbScheme = config.getSchemaName();
        this.dbUrl = config.getUrl();
        this.dbDriverClassname = config.getDriverClassName();
        this.dbUsername = config.getUser();
        this.dbPassword = config.getPassword();
        this.rootSchemeName = config.getRootSchemeName();
    }

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

    @Override
    public String getSchemaName() {
        return this.dbScheme;
    }

    @Override
    public String getRootSchemeName() {
        return this.rootSchemeName;
    }

    @Override
    public String getTestTable() {
        return "USER";
    }

    @Override
    public String toString() {
        return String.format("dbUrl: %s, schema: %s,  dbUsername: %s, pass: %s, root-schema: %s, test-table: %s", LogUtils.stripDbUrl(dbUrl),
                this.dbScheme, this.dbUsername, this.dbPassword, this.rootSchemeName, getTestTable());
    }
}
