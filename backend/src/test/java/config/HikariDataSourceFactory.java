package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.utils.LogUtils;

import java.sql.SQLException;
import java.util.Objects;

@Log4j2
public class HikariDataSourceFactory {

    private static HikariDataSource ADMIN_DATASOURCE;
    private static HikariDataSource CORE_DATASOURCE;

    static HikariDataSource createCoreDataSource() {
        try {
            if (Objects.isNull(CORE_DATASOURCE) || CORE_DATASOURCE.getConnection().isClosed()) {
                CORE_DATASOURCE = createHikariDataSource(coreDatabaseConfig());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + coreDatabaseConfig().getUrl(), e);
        }
        return CORE_DATASOURCE;
    }

    static HikariDataSource createAdminDataSource() {
        try {
            if (Objects.isNull(ADMIN_DATASOURCE) || ADMIN_DATASOURCE.getConnection().isClosed()) {
                ADMIN_DATASOURCE = createHikariDataSource(adminDatabaseConfig());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + adminDatabaseConfig().getUrl(), e);
        }
        return ADMIN_DATASOURCE;
    }

    private static HikariDataSource createHikariDataSource(String url, String password, String user, String driverClassName) {
        log.debug("Creating datasource for {}, {}, {}", LogUtils.stripDbUrl(url), password, user);
        HikariConfig config = new HikariConfig();
        config.setInitializationFailTimeout(-1);
        config.setIdleTimeout(1000);
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return new HikariDataSource(config);
    }

    private static HikariDataSource createHikariDataSource(DatabaseConfig config) {
        return createHikariDataSource(config.getUrl(), config.getPassword(), config.getUser(), config.getDriverClassName());
    }

    public static void closeAdminDataSource() {
        ADMIN_DATASOURCE.close();
    }

    static HikariDataSource createDataSource(DatabaseConfig databaseConfig) {
        return createHikariDataSource(databaseConfig);
    }

    private static DatabaseConfig adminDatabaseConfig() {
        return new DatabaseConfig() {

            @Override
            public String getUrl() {
                return "jdbc:mysql://localhost:3406/admin_db_test?autoReconnect=true&useSSL=false&rewriteBatchedStatements=true&lower_case_table_names=1&useLegacyDatetimeCode=false&serverTimezone=UTC";
            }

            @Override
            public String getDriverClassName() {
                return "com.mysql.cj.jdbc.Driver";
            }

            @Override
            public String getUser() {
                return "root";
            }

            @Override
            public String getPassword() {
                return "root";
            }
        };
    }

    private static DatabaseConfig coreDatabaseConfig() {
        return new DatabaseConfig() {

            @Override
            public String getUrl() {
                return "jdbc:mysql://localhost:3306/birzha_empty_schema?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            }

            @Override
            public String getDriverClassName() {
                return "com.mysql.cj.jdbc.Driver";
            }

            @Override
            public String getUser() {
                return "root";
            }

            @Override
            public String getPassword() {
                return "root";
            }
        };
    }
}
