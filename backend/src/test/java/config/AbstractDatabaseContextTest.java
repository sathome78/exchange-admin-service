package config;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.io.File.separator;


@Transactional
@Log4j2
public abstract class AbstractDatabaseContextTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final Map<String, DatabaseConfig> schemas = new ConcurrentHashMap<>();
    static final String RESOURCES_ROOT = "src/test/resources/";

    @Autowired
    private DatabaseConfig dbConfig;

    @Autowired
    @Qualifier("testAdminDataSource")
    protected DataSource dataSource;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeClass
    public static void beforeClass() {

    }

    @PostConstruct
    public void prepareTestSchema() throws SQLException {
        Preconditions.checkNotNull(dbConfig.getSchemaName(), "Scheme name must be defined");

        schemas.putIfAbsent(dbConfig.getSchemaName(), new DatabaseConfigImpl(dbConfig));
        String testSchemaUrl = createConnectionURL(dbConfig.getUrl(), dbConfig.getSchemaName());
        try {
            DriverManager.getConnection(testSchemaUrl, dbConfig.getUser(), dbConfig.getPassword());
        } catch (Exception e) {
            String dbServerUrl = createConnectionURL(dbConfig.getUrl(), "");
            Connection connection = DriverManager.getConnection(dbServerUrl, dbConfig.getUser(), dbConfig.getPassword());

            Statement statement = connection.createStatement();
            statement.execute(String.format("CREATE DATABASE %s;", dbConfig.getSchemaName()));

            DataSource rootDataSource = createRootDataSource(dbConfig, testSchemaUrl);

            populateSchema(rootDataSource);

            if (!isSchemeValid(rootDataSource)) {
                throw new RuntimeException("Test scheme " + dbConfig.getSchemaName() + " doesn't exist");
            }
        }
    }

    @AfterClass
    public static void afterClass() {
        schemas.values().forEach(config -> {
            try {
                String dbServerUrl = config.getUrl().replace(config.getRootSchemeName() + "?",  "?");
                Connection connection = DriverManager.getConnection(dbServerUrl, config.getUser(), config.getPassword());
                Statement statement = connection.createStatement();
                statement.execute(String.format("DROP DATABASE IF EXISTS %s;", config.getSchemaName()));
            } catch (SQLException e) {
                log.error("Failed to drop database in after class as", e);
            }
        });
    }

    @Rule
    public final SteppedTestPath steppedTestPath = new SteppedTestPath();

    @Before
    public final void setup() throws Exception {
        createFileTreeForTesting();
        before();
    }

    protected void before() {

    }

    @After
    public void after() {

    }

    private void createFileTreeForTesting() throws IOException {
        Path testRootFolder = Paths.get(RESOURCES_ROOT, getClass().getSimpleName(), steppedTestPath.getMethodName());
        Path expected = testRootFolder.resolve("expected");
        Path actual = testRootFolder.resolve("actual");
        Files.createDirectories(expected);
        FileUtils.deleteQuietly(actual.toFile());
    }

    @Configuration
    public static abstract class AppContextConfig {

        protected abstract String getSchema();

        @Autowired
        protected DatabaseConfig databaseConfig;

        @Bean
        public DatabaseConfig databaseConfig() {
            return new DatabaseConfigImpl(getSchema());
        }

        @Bean(name = "testAdminDataSource")
        public DataSource dataSource() {
            String dbUrl = databaseConfig.getUrl().replace(databaseConfig.getRootSchemeName() + "?", getSchema() + "?");
            log.debug("DB PROPS: DB URL: " + databaseConfig.getUrl());
            return createDataSource(databaseConfig.getUser(), databaseConfig.getPassword(), dbUrl, databaseConfig.getDriverClassName());
        }

        @Bean(name = "testAdminTemplate")
        public NamedParameterJdbcOperations slaveTemplate(@Qualifier("testAdminDataSource") DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean(name = "testJdbcOperations")
        public JdbcOperations jdbcOperations(@Qualifier("testAdminDataSource") DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("testAdminDataSource") DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        private HikariDataSource createDataSource(String user, String password, String url, String driverClassName) {
            HikariConfig config = new HikariConfig();
            config.setInitializationFailTimeout(-1);
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
            config.setDriverClassName(driverClassName);
            return new HikariDataSource(config);
        }
    }

    protected static String formatLine(Object key, Object value) {
        return String.format("|%-30s|%-100s|", String.valueOf(key), String.valueOf(value));
    }

    protected class SteppedTestPath extends TestName {

        private MutableInt step = new MutableInt();

        String nextExpectedStepPathForMethod() {
            return getRootMethodPath() + separator + "expected" + separator + getFileName();
        }

        String getFileName() {
            return String.format("step_%d.json", step.incrementAndGet());
        }

        private String getRootMethodPath() {
            return AbstractDatabaseContextTest.this.getClass().getSimpleName() + separator + getMethodName();
        }
    }

    private void populateSchema(DataSource rootDataSource) throws SQLException {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/structure/structure.sql"));
        populator.addScript(new ClassPathResource("db/structure/after_transaction_trigger.sql"));
        populator.populate(rootDataSource.getConnection());
    }

    private HikariDataSource createRootDataSource(DatabaseConfig dbConfig, String dbUrl) {
        HikariConfig config = new HikariConfig();
        config.setInitializationFailTimeout(-1);
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbConfig.getUser());
        config.setPassword(dbConfig.getPassword());
        return new HikariDataSource(config);
    }

    private String createConnectionURL(String dbUrl, String newSchemaName) {
        return dbUrl.replace(dbConfig.getRootSchemeName() + "?", newSchemaName + "?");
    }

    private boolean isSchemeValid(DataSource rootDataSource) {
        boolean result;
        try {
            final Statement statement = rootDataSource.getConnection().createStatement();
            result = statement.execute("SELECT 1 FROM CURSORS");
        } catch (SQLException e) {
            logger.error(String.format("Failed to check scheme %s validity", dbConfig.getSchemaName()), e);
            throw new RuntimeException(e);
        }
        return result;
    }
}
