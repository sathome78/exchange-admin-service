package me.exrates.adminservice.services;

import config.AbstractDatabaseContextTest;
import config.AsyncTransactionsTestConfig;
import config.DataComparisonTest;
import config.HSQLConfiguration;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import me.exrates.adminservice.core.repository.impl.CoreTransactionRepositoryImpl;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.repository.AdminTransactionRepository;
import me.exrates.adminservice.repository.CursorRepository;
import me.exrates.adminservice.repository.impl.CursorRepositoryImpl;
import me.exrates.adminservice.services.impl.SyncTransactionServiceImpl;
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
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        AsyncTransactionsTestConfig.class,
        SyncTransactionServiceTest.InnerConfig.class
})
public class SyncTransactionServiceTest extends DataComparisonTest {

    @Autowired
    private SyncTransactionService syncTransactionService;

    @Autowired
    private ExchangeRatesService exchangeRatesService;

    @Autowired
    private AdminTransactionRepository adminTransactionRepository;

    @Before
    public void before() {
        when(exchangeRatesService.getCachedRates()).thenReturn(getTestRates());
    }

    @Test
    // todo this not complete test it's just for flow steps debugging
    public void syncTransactions_nonEmpty() {
        when(adminTransactionRepository.batchInsert(anyList())).thenReturn(Boolean.TRUE);
        syncTransactionService.syncTransactions();

//        await().atMost(5, TimeUnit.SECONDS)
//                .until(() -> {
//                    verify(transactionsUpdateEventListener).handleTransactionsUpdateEvent(any());
//                    return true;
//                });
    }

    private Map<String, RateDto> getTestRates() {
        Map<String, RateDto> rateDtoMap = new HashMap<>(3);
        rateDtoMap.put("USD", RateDto.builder().currencyName("USD").btcRate(BigDecimal.TEN).usdRate(BigDecimal.ONE).build());
        rateDtoMap.put("BTC", RateDto.builder().currencyName("BTC").btcRate(BigDecimal.ONE).usdRate(BigDecimal.TEN).build());
        rateDtoMap.put("LTC", RateDto.builder().currencyName("LTC").btcRate(BigDecimal.TEN).usdRate(BigDecimal.TEN).build());
        return rateDtoMap;
    }

    @Configuration
    @Profile("test")
    @Import({
            HSQLConfiguration.class
    })
    static class InnerConfig extends AbstractDatabaseContextTest.AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE)
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_NP_TEMPLATE)
        private NamedParameterJdbcOperations adminNPJdbcOperations;

//        @Autowired
//        @Qualifier(TEST_CORE_DATASOURCE)
//        public DataSource dataSource;

        @Autowired
        private ApplicationEventPublisher applicationEventPublisher;

        @Bean
        public AdminTransactionRepository adminTransactionRepository() {
            return Mockito.mock(AdminTransactionRepository.class);
        }

        @Bean
        public CursorRepository testCursorRepository() {
            return new CursorRepositoryImpl(adminNPJdbcOperations);
        }

        @Bean
        public ExchangeRatesService testExchangeRatesService() {
            return Mockito.mock(ExchangeRatesService.class);
        }

        @Bean
        public CoreTransactionRepository testCoreTransactionRepository() {
            return new CoreTransactionRepositoryImpl(coreNPJdbcOperations);
        }

        @Bean
        public SyncTransactionService testSyncTransactionService() {
            return new SyncTransactionServiceImpl(adminTransactionRepository(), applicationEventPublisher,
                    testCursorRepository(), testCoreTransactionRepository(), testExchangeRatesService());
        }

        @Bean
        public UpdateTransactionService updateTransactionService() {
            return Mockito.mock(UpdateTransactionService.class);
        }

        @Override
        protected String getSchema() {
            return "SyncTransactionServiceTest";
        }
    }
}