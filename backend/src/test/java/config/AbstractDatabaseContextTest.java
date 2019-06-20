package config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static java.io.File.separator;


@Transactional
@Log4j2
public abstract class AbstractDatabaseContextTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    static final String RESOURCES_ROOT = "src/test/resources/";

    protected static HikariDataSource adminDatasource;

    protected static final String TEST_ADMIN_DATASOURCE = "testAdminDataSource";
    protected static final String TEST_CORE_DATASOURCE = "testCoreDataSource";
    protected static final String TEST_ADMIN_NP_TEMPLATE = "testAdminNPTemplate";
    protected static final String TEST_ADMIN_TEMPLATE = "testAdminTemplate";
    protected static final String TEST_CORE_NP_TEMPLATE = "testCoreTemplate";
    protected static final String TEST_ADMIN_JDBC_OPS = "adminJdbcOperations";


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
    @Qualifier(TEST_ADMIN_TEMPLATE)
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
        adminDatasource = HikariDataSourceFactory.createAdminDataSource();
        rebuildSchema(adminDatasource);
        executeMigrations(adminDatasource);
    }

    @AfterClass
    public static void afterClass() {
//        HikariDataSourceFactory.closeAdminDataSource();
    }

    @Rule
    public final SteppedTestPath steppedTestPath = new SteppedTestPath();

    @Before
    public final void setup() throws Exception {
        createFileTreeForTesting();
        before();
    }

    protected void before() throws SQLException {

    }

    @After
    public void after() {
//        dataSources.values().forEach(HikariDataSource::close);
//        dataSources.clear();
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

        @Bean(name = TEST_ADMIN_DATASOURCE)
        public DataSource adminDataSource() {
            return HikariDataSourceFactory.createAdminDataSource();
        }

        @Bean(name = TEST_ADMIN_TEMPLATE)
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

    private static void rebuildSchema(DataSource dataSource) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db/tools/R__prepare_database.sql"));
        try {
            populator.populate(dataSource.getConnection());
        } catch (SQLException e) {
            log.error(e);
           throw new RuntimeException("Failed run create table scripts " + e.getMessage(), e);
        }
    }

    private static void executeMigrations(DataSource dataSource) {
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
}
