package config;

public interface DatabaseConfig {

    String ADMIN_PREFIX = "ADMIN_";
    String ADMIN_DB_CONFIG = "adminDBConfig";

    String getUrl();

    String getDriverClassName();

    String getUser();

    String getPassword();

    String getSchemaName();

    String getRootSchemeName();

    String getTestTable();
}
