package me.exrates.adminservice.configurations;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.utils.LogUtils;

import java.util.concurrent.TimeUnit;

@Log4j2
public abstract class DatabaseConfiguration {

    protected abstract String getDatabaseUrl();

    protected abstract String getDatabaseUsername();

    protected abstract String getDatabasePassword();

    protected abstract String getDatabaseDriverClassName();

    protected HikariDataSource createDataSource() {
        log.debug("Creating datasource with -h: {}, -u: {}, -p: {}", LogUtils.stripDbUrl(getDatabaseUrl()), getDatabaseUsername(), getDatabasePassword());
        HikariConfig config = new HikariConfig();
        config.setInitializationFailTimeout(-1);
        config.setJdbcUrl(getDatabaseUrl());
        config.setUsername(getDatabaseUsername());
        config.setPassword(getDatabasePassword());
        config.setDriverClassName(getDatabaseDriverClassName());
        config.setMaximumPoolSize(10);
        config.setLeakDetectionThreshold(60000);
        config.setMinimumIdle(1);
        config.setIdleTimeout(30000);
        return new HikariDataSource(config);
    }
}
