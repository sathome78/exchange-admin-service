package config;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import config.impl.AdminDatabaseConfigImpl;
import config.impl.CoreDatabaseConfigImpl;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.utils.LogUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Qualifier(DatabaseConfig.CORE_DB_CONFIG)
    private DatabaseConfig  coreDbConfig;

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
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeClass
    public static void beforeClass() {

    }

    @PostConstruct
    public void prepareTestSchema() throws SQLException, IOException {
        List<String> adminPaths = ImmutableList.of("db/structure/admin/structure.sql",
                "db/structure/admin/insert-data.sql");

        List<String> adminRowSqls = ImmutableList.of("db/structure/admin/trans-trigger-1.sql",
//                "db/structure/admin/trans-trigger-2.sql",
                "db/structure/admin/trans-trigger-3.sql");
//                "db/structure/admin/trans-trigger-4.sql");
        initSchema(new AdminDatabaseConfigImpl(adminDbConfig), adminPaths, Collections.emptyList());

        List<String> corePaths = ImmutableList.of("db/structure/core/dump.sql");
        initSchema(new CoreDatabaseConfigImpl(coreDbConfig), corePaths, Collections.emptyList());
    }

    private void initSchema(DatabaseConfig databaseConfig, List<String> resourcePaths, List<String> sourceFiles) throws SQLException {
        Preconditions.checkNotNull(databaseConfig.getSchemaName(), "Admin Scheme name must be defined");
        schemas.putIfAbsent(databaseConfig.getSchemaName(), databaseConfig);

        String testSchemaUrl = createConnectionURL(databaseConfig.getUrl(), databaseConfig.getSchemaName(), databaseConfig);

        try {
            DriverManager.getConnection(testSchemaUrl, databaseConfig.getUser(), databaseConfig.getPassword());
        } catch (Exception e) {
            String dbServerUrl = createConnectionURL(databaseConfig.getUrl(), "", databaseConfig);
            Connection connection = DriverManager.getConnection(dbServerUrl, databaseConfig.getUser(), databaseConfig.getPassword());

            Statement statement = connection.createStatement();
            statement.execute(String.format("CREATE DATABASE %s;", databaseConfig.getSchemaName()));

            DataSource rootDataSource = createRootDataSource(databaseConfig, testSchemaUrl);

            populateSchema(rootDataSource, resourcePaths);

            pupulateFromRawSql(rootDataSource, sourceFiles);


            if (!isSchemeValid(rootDataSource, databaseConfig)) {
                throw new RuntimeException("Test scheme " + databaseConfig.getSchemaName() + " doesn't exist");
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
    @Profile("test")
    public static abstract class AppContextConfig {

        protected abstract String getSchema();

        @Autowired
        @Qualifier(DatabaseConfig.ADMIN_DB_CONFIG)
        protected DatabaseConfig adminDatabaseConfig;

        @Autowired
        @Qualifier(DatabaseConfig.CORE_DB_CONFIG)
        protected DatabaseConfig coreDatabaseConfig;

        @Bean(DatabaseConfig.ADMIN_DB_CONFIG)
        public DatabaseConfig adminDatabaseConfig() {
            return new AdminDatabaseConfigImpl(getSchema());
        }

        @Bean(DatabaseConfig.CORE_DB_CONFIG)
        public DatabaseConfig coreDatabaseConfig() {
            return new CoreDatabaseConfigImpl(getSchema());
        }

        @Bean(name = TEST_ADMIN_DATASOURCE)
        public DataSource adminDataSource() {
            String dbUrl = adminDatabaseConfig.getUrl().replace(adminDatabaseConfig.getRootSchemeName() + "?", adminDatabaseConfig.getSchemaName() + "?");
            log.debug("DB PROPS: DB URL: " + LogUtils.stripDbUrl(dbUrl));
            log.debug("DB CONFIG: ADMIN: " + adminDatabaseConfig);
            return createDataSource(adminDatabaseConfig.getUser(), adminDatabaseConfig.getPassword(), dbUrl, adminDatabaseConfig.getDriverClassName());
        }

        @Bean(name = TEST_CORE_DATASOURCE)
        public DataSource coreDataSource() {
            String dbUrl = coreDatabaseConfig.getUrl().replace(coreDatabaseConfig.getRootSchemeName() + "?", coreDatabaseConfig.getSchemaName() + "?");
            log.debug("DB PROPS: DB URL: " + LogUtils.stripDbUrl(dbUrl));
            log.debug("DB CONFIG: CORE: " + coreDatabaseConfig);
            return createDataSource(coreDatabaseConfig.getUser(), coreDatabaseConfig.getPassword(), dbUrl, coreDatabaseConfig.getDriverClassName());
        }

        @Bean(name = TEST_ADMIN_JDBC_OPS)
        public JdbcOperations adminJdbcOperations(@Qualifier(TEST_ADMIN_DATASOURCE) DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }


        @Bean(name = TEST_ADMIN_NP_TEMPLATE)
        public NamedParameterJdbcOperations adminNPTemplate(@Qualifier(TEST_ADMIN_DATASOURCE) DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean(name = TEST_CORE_NP_TEMPLATE)
        public NamedParameterJdbcOperations coreNPTemplate(@Qualifier(TEST_CORE_DATASOURCE) DataSource dataSource) {
            return new NamedParameterJdbcTemplate(dataSource);
        }

        @Bean
        public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier(TEST_ADMIN_DATASOURCE) DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        private HikariDataSource createDataSource(String user, String password, String url, String driverClassName) {
            log.debug("Creating datasource for {}, {}, {}", LogUtils.stripDbUrl(url), password, user);
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

    private void populateSchema(DataSource dataSource, List<String> resourcePaths) throws SQLException {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        resourcePaths.forEach(path -> populator.addScript(new ClassPathResource(path)));
        populator.populate(dataSource.getConnection());
    }

    private void pupulateFromRawSql(DataSource dataSource, List<String> paths) {
        paths.forEach(p -> {
            File source = null;
            try {
                source = new ClassPathResource(p).getFile();
            } catch (IOException e) {
                String message = "Failed to find file for path " + p;
                log.error(message, e);
                throw new RuntimeException(message, e);
            }
            String rawSql = "";
            try {
                Stream<String> lines = Files.lines(source.toPath());
                rawSql = lines.collect(Collectors.joining(" "));
                final Statement statement = dataSource.getConnection().createStatement();
                statement.execute(rawSql);

            } catch (IOException e) {
                String message = "Failed to read file for file name: " + p;
                log.error(message, e);
                throw new RuntimeException(message, e);
            } catch (SQLException e) {
                String message = "Failed to execute sql: " + rawSql;
                log.error(message, e);
                throw new RuntimeException(message, e);
            }
        });
    }

    private HikariDataSource createRootDataSource(DatabaseConfig dbConfig, String dbUrl) {
        HikariConfig config = new HikariConfig();
        config.setInitializationFailTimeout(-1);
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbConfig.getUser());
        config.setPassword(dbConfig.getPassword());
        return new HikariDataSource(config);
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
