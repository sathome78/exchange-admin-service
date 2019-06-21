package me.exrates.adminservice.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.domain.api.BalanceDto;
import me.exrates.adminservice.repository.WalletBalancesRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = WalletBalancesRepositoryImplTest.InnerConfig.class)
public class WalletBalancesRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier("testWalletBalancesRepository")
    private WalletBalancesRepository walletBalancesRepository;

    @Override
    protected void before() {
        setDatabaseType(ADMIN_DATABASE_TYPE);
        try {
            truncateTables(WalletBalancesRepository.TABLE_NAME);

            String sql = "INSERT INTO " + WalletBalancesRepository.TABLE_NAME
                    + " (currency_id, currency_name) VALUES (1, \'TEST_COIN\');";
            prepareTestData(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllWalletBalances_ok() {
        List<BalanceDto> result = walletBalancesRepository.getAllWalletBalances();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getAllWalletBalances_not_found() throws Exception {
        truncateTables(WalletBalancesRepository.TABLE_NAME);

        List<BalanceDto> result = walletBalancesRepository.getAllWalletBalances();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void updateCurrencyWalletBalances_ok() {
        final List<BalanceDto> list = Collections.singletonList(BalanceDto.builder()
                .currencyName("TEST_COIN")
                .balance(BigDecimal.TEN)
                .lastUpdatedAt(LocalDateTime.now())
                .build());
        around()
                .withSQL("SELECT * FROM " + WalletBalancesRepository.TABLE_NAME)
                .run(() -> walletBalancesRepository.updateCurrencyWalletBalances(list));
    }

    @Configuration
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_ADMIN_NP_TEMPLATE)
        protected NamedParameterJdbcOperations adminNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_JDBC_OPS)
        protected JdbcOperations adminJdbcOperations;

        @Override
        protected String getSchema() {
            return "WalletBalancesRepositoryImplTest";
        }

        @Bean("testWalletBalancesRepository")
        public WalletBalancesRepository walletBalancesRepository() {
            return new WalletBalancesRepositoryImpl(adminNPJdbcOperations, adminJdbcOperations);
        }
    }
}