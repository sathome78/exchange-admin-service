package config;

import com.google.common.base.Preconditions;
import config.impl.AdminDatabaseConfigImpl;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.utils.LogUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.flywaydb.core.Flyway;
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
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.io.File.separator;


@Transactional
@Log4j2
public abstract class AbstractDatabaseContextTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static final Map<String, DatabaseConfig> schemas = new ConcurrentHashMap<>();
    static final String RESOURCES_ROOT = "src/test/resources/";

    protected static final String TEST_ADMIN_DATASOURCE = "testAdminDataSource";
    protected static final String TEST_CORE_DATASOURCE = "testCoreDataSource";
    protected static final String TEST_ADMIN_NP_TEMPLATE = "testAdminTemplate";
    protected static final String TEST_CORE_NP_TEMPLATE = "testCoreTemplate";
    protected static final String TEST_ADMIN_JDBC_OPS = "adminJdbcOperations";

    @Autowired
    @Qualifier(DatabaseConfig.ADMIN_DB_CONFIG)
    private DatabaseConfig  adminDbConfig;

    @Autowired
    @Qualifier(TEST_ADMIN_DATASOURCE)
    protected DataSource dataSource;

    @Autowired
    @Qualifier(TEST_ADMIN_NP_TEMPLATE)
    protected NamedParameterJdbcOperations adminNPJdbcOperations;

    @Autowired
    @Qualifier(TEST_CORE_NP_TEMPLATE)
    protected NamedParameterJdbcOperations coreNPJdbcOperations;

    @Autowired
    @Qualifier(TEST_ADMIN_JDBC_OPS)
    protected JdbcOperations adminJdbcOperations;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeClass
    public static void beforeClass() {

    }

    @PostConstruct
    public void prepareTestSchema() throws SQLException {
        initSchema(new AdminDatabaseConfigImpl(adminDbConfig));
    }

    private void initSchema(DatabaseConfig databaseConfig) throws SQLException {
        Preconditions.checkNotNull(databaseConfig.getSchemaName(), "Admin Scheme name must be defined");
        schemas.putIfAbsent(databaseConfig.getSchemaName(), databaseConfig);

        String testSchemaUrl = createConnectionURL(databaseConfig.getUrl(), databaseConfig.getSchemaName(), databaseConfig);

        try {
            final Connection connection = DriverManager.getConnection(testSchemaUrl, databaseConfig.getUser(), databaseConfig.getPassword());
            connection.close();
        } catch (Exception e) {
            String dbServerUrl = createConnectionURL(databaseConfig.getUrl(), "", databaseConfig);
            Connection connection = DriverManager.getConnection(dbServerUrl, databaseConfig.getUser(), databaseConfig.getPassword());

            Statement statement = connection.createStatement();
            statement.execute(String.format("CREATE DATABASE %s;", databaseConfig.getSchemaName()));

            DataSource rootDataSource = HikariDataSourceFactory.createRootDataSource(databaseConfig, testSchemaUrl);

            executeMigrations();

            if (!isSchemeValid(rootDataSource, databaseConfig)) {
                throw new RuntimeException("Test scheme " + databaseConfig.getSchemaName() + " doesn't exist");
            }

            connection.close();
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
                connection.close();
            } catch (SQLException e) {
                log.error("Failed to drop database in after class as", e);
            }
        });
        schemas.clear();
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
    @Profile("test")
    @Import({
            CoreTestDatabaseConfiguration.class
    })
    public static abstract class AppContextConfig {

        protected abstract String getSchema();

        @Autowired
        @Qualifier(DatabaseConfig.ADMIN_DB_CONFIG)
        protected DatabaseConfig adminDatabaseConfig;

        @Bean(DatabaseConfig.ADMIN_DB_CONFIG)
        public DatabaseConfig adminDatabaseConfig() {
            return new AdminDatabaseConfigImpl(getSchema());
        }

        @Bean(name = TEST_ADMIN_DATASOURCE)
        public DataSource adminDataSource() {
            String dbUrl = adminDatabaseConfig.getUrl().replace(adminDatabaseConfig.getRootSchemeName() + "?", adminDatabaseConfig.getSchemaName() + "?");
            log.debug("DB PROPS: DB URL: " + LogUtils.stripDbUrl(dbUrl));
            log.debug("DB CONFIG: ADMIN: " + adminDatabaseConfig);
            return HikariDataSourceFactory.createDataSource(adminDatabaseConfig.getUser(), adminDatabaseConfig.getPassword(), dbUrl, adminDatabaseConfig.getDriverClassName());
        }

        @Bean(name = TEST_ADMIN_JDBC_OPS)
        public JdbcOperations adminJdbcOperations(@Qualifier(TEST_ADMIN_DATASOURCE) DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }


        @Bean(name = TEST_ADMIN_NP_TEMPLATE)
        public NamedParameterJdbcOperations adminNPTemplate(@Qualifier(TEST_ADMIN_DATASOURCE) DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean
        public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier(TEST_ADMIN_DATASOURCE) DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
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

    private void executeMigrations() {

        final String [] locations = {
                "db/migration",
                "db/data/admin/"
        };

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .load();
        flyway.migrate();
    }

    private String createConnectionURL(String dbUrl, String newSchemaName, DatabaseConfig config) {
        return dbUrl.replace(config.getRootSchemeName() + "?", newSchemaName + "?");
    }

    private boolean isSchemeValid(DataSource rootDataSource, DatabaseConfig config) {
        boolean result;
        try {
            final Statement statement = rootDataSource.getConnection().createStatement();
            result = statement.execute("SELECT 1 FROM " + config.getTestTable());
        } catch (SQLException e) {
            logger.error(String.format("Failed to check scheme %s validity", config.getSchemaName()), e);
            throw new RuntimeException(e);
        }
        return result;
    }
}
