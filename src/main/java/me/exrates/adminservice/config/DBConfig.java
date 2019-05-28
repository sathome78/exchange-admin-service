package me.exrates.adminservice.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.concurrent.TimeUnit;

public abstract class DBConfig {

    protected abstract String getDatabaseUrl();

    protected abstract String getDatabaseUsername();

    protected abstract String getDatabasePassword();

    protected abstract String getDatabaseDriverClassName();

    protected HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setInitializationFailTimeout(-1);
        config.setJdbcUrl(getDatabaseUrl());
        config.setUsername(getDatabaseUsername());
        config.setPassword(getDatabasePassword());
        config.setDriverClassName(getDatabaseDriverClassName());
        config.setMaximumPoolSize(2);
        config.setLeakDetectionThreshold(TimeUnit.MILLISECONDS.convert(45, TimeUnit.SECONDS));
        config.setMinimumIdle(1);
        config.setIdleTimeout(30000);
        return new HikariDataSource(config);
    }
}
