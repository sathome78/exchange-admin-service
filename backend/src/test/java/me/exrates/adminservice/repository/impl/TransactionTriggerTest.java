package me.exrates.adminservice.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import me.exrates.adminservice.core.repository.impl.CoreTransactionRepositoryImpl;
import me.exrates.adminservice.domain.CoreCursor;
import me.exrates.adminservice.repository.AdminTransactionRepository;
import me.exrates.adminservice.repository.CursorRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TransactionTriggerTest.InnerConfig.class)
public class TransactionTriggerTest extends DataComparisonTest {

    @Autowired
    @Qualifier("testAdminTransactionRepository")
    private AdminTransactionRepository adminTransactionRepository;

    @Test
    public void autoSyncOperations() {
        around()
                .withSQL("SELECT * FROM USER_ANNUAL_INSIGHTS")
                .run(() -> adminTransactionRepository.batchInsert(getTestTransactions()));
    }

    private List<CoreTransaction> getTestTransactions() {
        List<CoreTransaction> transactions = new ArrayList<>(20);
        return transactions;
    }

    @Configuration
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier("testJdbcOperations")
        public JdbcOperations jdbcOperations;

        @Override
        protected String getSchema() {
            return "TransactionTriggerTest";
        }

        @Bean("testAdminTransactionRepository")
        public AdminTransactionRepository adminTransactionRepository() {
            return new AdminTransactionRepositoryImpl(jdbcOperations);
        }
    }
}
