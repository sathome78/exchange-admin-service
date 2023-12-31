package me.exrates.adminservice.services.impl;

import com.google.common.collect.ImmutableList;
import config.AbstractDatabaseContextTest;
import config.AsyncTransactionsTestConfig;
import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.repository.CoreWalletRepository;
import me.exrates.adminservice.core.repository.impl.CoreTransactionRepositoryImpl;
import me.exrates.adminservice.core.repository.impl.CoreUserRepositoryImpl;
import me.exrates.adminservice.core.repository.impl.CoreWalletRepositoryImpl;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.domain.enums.RefillEventEnum;
import me.exrates.adminservice.repository.TransactionRepository;
import me.exrates.adminservice.repository.UserInsightRepository;
import me.exrates.adminservice.repository.impl.TransactionRepositoryImpl;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.services.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        AsyncTransactionsTestConfig.class,
        TransactionServiceTest.InnerConfig.class
})
public class TransactionServiceTest extends DataComparisonTest {


    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ExchangeRatesService exchangeRatesService;

    @Before
    public void before() {
        setDatabaseType(ADMIN_DATABASE_TYPE);

        when(exchangeRatesService.getCachedRates()).thenReturn(getTestRates());
    }

    @Test
    public void syncTransactions_nonEmpty() {
        final String selectAllInsights = "SELECT * FROM " + TransactionRepository.TABLE;
        final String selectInsights = "SELECT * FROM " + UserInsightRepository.TABLE;

        around()
                .withSQL(selectAllInsights, selectInsights)
                .run(() -> transactionService.syncTransactions());
    }

    @Test
    public void testGetDailyCommissionRevenue() {
        final Map<String, BigDecimal> revenue = transactionService.getDailyCommissionRevenue();

        assertEquals("0.402", revenue.get("BTC").stripTrailingZeros().toPlainString());
        assertEquals("4960", revenue.get("USD").stripTrailingZeros().toPlainString());
    }

    @Test
    public void testGetDailyInnerTradeVolume() {
        final Map<String, BigDecimal> revenue = transactionService.getDailyInnerTradeVolume();

        assertEquals("4.02", revenue.get("BTC").stripTrailingZeros().toPlainString());
        assertEquals("49600", revenue.get("USD").stripTrailingZeros().toPlainString());
    }

    @Test
    public void testGetAllUsersRefillEvents() {
        final ImmutableList<Integer> userIds = ImmutableList.of(1, 2, 3);
        final Map<Integer, List<CoreTransaction>> transactions = transactionService.findAllTransactions(userIds);
        final Map<Integer, Set<RefillEventEnum>> usersRefillEvents = transactionService.getAllUsersRefillEvents(transactions, userIds);

        assertTrue(usersRefillEvents.get(1).isEmpty());

        final Set<RefillEventEnum> secondUserEvents = usersRefillEvents.get(2);
        assertEquals(2, secondUserEvents.size());
        assertTrue(secondUserEvents.contains(RefillEventEnum.ZEROED));
        assertTrue(secondUserEvents.contains(RefillEventEnum.REANIMATED));

        final Set<RefillEventEnum> thirdUserEvents = usersRefillEvents.get(3);
        assertEquals(1, thirdUserEvents.size());
        assertTrue(thirdUserEvents.contains(RefillEventEnum.ZEROED));
        assertTrue(!thirdUserEvents.contains(RefillEventEnum.REANIMATED));
    }

    private Map<String, RateDto> getTestRates() {
        BigDecimal crossRate = new BigDecimal(0.00012548);
        Map<String, RateDto> rateDtoMap = new HashMap<>(3);
        rateDtoMap.put("USD", RateDto.builder().currencyName("USD").btcRate(BigDecimal.TEN).usdRate(BigDecimal.ONE).rateBtcForOneUsd(crossRate).build());
        rateDtoMap.put("BTC", RateDto.builder().currencyName("BTC").btcRate(BigDecimal.ONE).usdRate(BigDecimal.TEN).rateBtcForOneUsd(crossRate).build());
        rateDtoMap.put("LTC", RateDto.builder().currencyName("LTC").btcRate(BigDecimal.TEN).usdRate(BigDecimal.TEN).rateBtcForOneUsd(crossRate).build());
        return rateDtoMap;
    }

    @Configuration
    @Profile("test")
    static class InnerConfig extends AbstractDatabaseContextTest.AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_CORE_TEMPLATE) // it's ok bean will be imported later
        private JdbcOperations corJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_NP_TEMPLATE)
        private NamedParameterJdbcOperations adminNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_JDBC_OPS)
        private JdbcOperations adminJdbcOperations;

        @Autowired
        private ApplicationEventPublisher applicationEventPublisher;

        @Bean
        CoreUserRepository coreUserRepository() {
            return new CoreUserRepositoryImpl(coreNPJdbcOperations, corJdbcOperations);
        }

        @Bean
        CoreWalletRepository coreWalletRepository() {
            return new CoreWalletRepositoryImpl(coreUserRepository(), coreNPJdbcOperations, testCoreTransactionRepository());
        }

        @Bean
        public TransactionRepository adminTransactionRepository() {
            return new TransactionRepositoryImpl(adminJdbcOperations, adminNPJdbcOperations, coreUserRepository());
        }

        @Bean
        public ExchangeRatesService testExchangeRatesService() {
            return Mockito.mock(ExchangeRatesService.class);
        }

        @Bean
        public CoreTransactionRepository testCoreTransactionRepository() {
            return new CoreTransactionRepositoryImpl(coreNPJdbcOperations, coreUserRepository());
        }

        @Bean
        public TransactionService transactionService() {
            return new TransactionServiceImpl(adminTransactionRepository(), applicationEventPublisher,
                    testCoreTransactionRepository(), testExchangeRatesService());
        }

        @Override
        protected String getSchema() {
            return "TransactionServiceTest";
        }
    }
}
