package me.exrates.adminservice.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.domain.RateHistoryDto;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.repository.ExchangeRatesRepository;
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
@ContextConfiguration(classes = ExchangeRatesRepositoryImplTest.InnerConfig.class)
public class ExchangeRatesRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier("testExchangeRatesRepository")
    private ExchangeRatesRepository exchangeRatesRepository;

    @Override
    protected void before() {
        try {
            truncateTables(ExchangeRatesRepository.TABLE_NAME_1, ExchangeRatesRepository.TABLE_NAME_2);

            String sql1 = "INSERT INTO " + ExchangeRatesRepository.TABLE_NAME_1
                    + " (currency_id, currency_name) VALUES (1, \'TEST_COIN\');";
            byte[] content = {1};
            String sql2 = "INSERT INTO " + ExchangeRatesRepository.TABLE_NAME_2
                    + " (content) VALUES (\'" + content + "\');";
            prepareTestData(sql1, sql2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getAllExchangeRates_ok() {
        List<RateDto> result = exchangeRatesRepository.getAllExchangeRates();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getAllExchangeRates_not_found() throws Exception {
        truncateTables(ExchangeRatesRepository.TABLE_NAME_1);

        List<RateDto> result = exchangeRatesRepository.getAllExchangeRates();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void updateCurrencyExchangeRates_ok() {
        final List<RateDto> list = Collections.singletonList(RateDto.builder()
                .currencyName("TEST_COIN")
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.TEN)
                .build());
        around()
                .withSQL("SELECT * FROM " + ExchangeRatesRepository.TABLE_NAME_1)
                .run(() -> exchangeRatesRepository.updateCurrencyExchangeRates(list));
    }

    @Test
    public void saveCurrencyExchangeRatesHistory_ok() {
        around()
                .withSQL("SELECT * FROM " + ExchangeRatesRepository.TABLE_NAME_2)
                .run(() -> exchangeRatesRepository.saveCurrencyExchangeRatesHistory(new byte[]{}));
    }

    @Test
    public void getExchangeRatesHistoryByDate_ok() {
        final LocalDateTime now = LocalDateTime.now();

        List<RateHistoryDto> result = exchangeRatesRepository.getExchangeRatesHistoryByDate(now.minusDays(1), now.plusDays(1));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getExchangeRatesHistoryByDate_not_found() throws Exception {
        truncateTables(ExchangeRatesRepository.TABLE_NAME_2);

        final LocalDateTime now = LocalDateTime.now();

        List<RateHistoryDto> result = exchangeRatesRepository.getExchangeRatesHistoryByDate(now.minusDays(2), now.plusDays(1));

        assertNotNull(result);
        assertEquals(0, result.size());
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
            return "ExchangeRatesRepositoryImplTest";
        }

        @Bean("testExchangeRatesRepository")
        public ExchangeRatesRepository exchangeRatesRepository() {
            return new ExchangeRatesRepositoryImpl(adminNPJdbcOperations, adminJdbcOperations);
        }
    }
}