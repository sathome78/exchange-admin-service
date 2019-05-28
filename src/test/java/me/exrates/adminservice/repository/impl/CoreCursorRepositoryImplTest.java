package me.exrates.adminservice.repository.impl;

import config.AbstractDatabaseContextTest;
import config.DataComparisonTest;
import me.exrates.adminservice.domain.CoreCursor;
import me.exrates.adminservice.repository.CoreCursorRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = CoreCursorRepositoryImplTest.InnerConfig.class)
public class CoreCursorRepositoryImplTest extends DataComparisonTest {

    @Autowired
    private CoreCursorRepository coreCursorRepository;

    @Override
    protected void before() {
        try {
            truncateTables(CoreCursorRepository.TABLE_NAME);
            String sql = "INSERT INTO " + CoreCursorRepository.TABLE_NAME
                    + " (table_name, table_column, last_id) VALUE (\'table1\', \'id\', 42342134);";
            prepareTestData(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void findLastByTable_ok() {
        final Long result = coreCursorRepository.findLastByTable("table1");
        assertEquals(42342134L, result.longValue());
    }

    @Test
    public void findLastByTable_notFound() {
        final Long result = coreCursorRepository.findLastByTable("table2");
        assertEquals(-1L, result.longValue());
    }

    @Test
    public void save() {
        CoreCursor coreCursor = getTestCursor();
        around()
                .withSQL("SELECT * FROM " + CoreCursorRepository.TABLE_NAME)
                .run(() -> coreCursorRepository.save(coreCursor));
        assertNotNull(coreCursor.getModified());
    }

    @Test
    public void update() throws SQLException {
        final String sql = "INSERT INTO " + CoreCursorRepository.TABLE_NAME
                + " (table_name, table_column, last_id) VALUE (\'table2\', \'id\', 45);";

        prepareTestData(sql);
        CoreCursor update = CoreCursor.builder()
                .tableName("table2")
                .tableColumn("id")
                .cursorPosition(10)
                .build();
        around()
                .withSQL("SELECT * FROM " + CoreCursorRepository.TABLE_NAME)
                .run(() -> coreCursorRepository.save(update));
    }

    @Test
    public void findAll() {
        final Collection<CoreCursor> all = coreCursorRepository.findAll();
        assertEquals(1, all.size());

        final String tableName = all.stream().findFirst().get().getTableName();
        assertEquals("table1", tableName);
    }

    private CoreCursor getTestCursor() {
        return CoreCursor.builder()
                .tableName("table2")
                .tableColumn("column_id")
                .cursorPosition(Integer.MAX_VALUE)
                .build();
    }

    @Configuration
    static class InnerConfig extends AbstractDatabaseContextTest.AppContextConfig {

        @Autowired
        @Qualifier("testAdminTemplate")
        private NamedParameterJdbcTemplate jdbcTemplate;

        @Override
        protected String getSchema() {
            return "CoreCursorRepositoryImplTest";
        }

        @Bean
        CoreCursorRepository coreCursorRepository() {
            return new CoreCursorRepositoryImpl(jdbcTemplate);
        }
    }
}
