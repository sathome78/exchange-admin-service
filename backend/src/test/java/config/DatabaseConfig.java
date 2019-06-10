package config;

public interface DatabaseConfig {

    String ADMIN_PREFIX = "ADMIN_";
    String CORE_PREFIX = "CORE_";
    String ADMIN_DB_CONFIG = "adminDBConfig";
    String CORE_DB_CONFIG = "coreDBConfig";


    String getUrl();

    String getDriverClassName();

    String getUser();

    String getPassword();

    String getSchemaName();

    String getRootSchemeName();

    String getTestTable();
}
