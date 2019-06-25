package me.exrates.adminservice.services.impl;

import config.AsyncTransactionsTestConfig;
import config.DataComparisonTest;
import me.exrates.adminservice.core.repository.CoreOrderRepository;
import me.exrates.adminservice.core.repository.impl.CoreOrderRepositoryImpl;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.repository.ClosedOrderRepository;
import me.exrates.adminservice.repository.impl.ClosedOrderRepositoryImpl;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.services.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
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
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        AsyncTransactionsTestConfig.class,
        OrderServiceTest.InnerConfig.class
})
public class OrderServiceTest extends DataComparisonTest {


    @Autowired
    private OrderService orderService;

    @Autowired
    private ExchangeRatesService exchangeRatesService;

    @Before
    public void before() {
        setDatabaseType(ADMIN_DATABASE_TYPE);

        when(exchangeRatesService.getCachedRates()).thenReturn(getTestRates());
    }

    @Test
    public void syncTransactions_nonEmpty() {
        final String selectOrders = "SELECT * FROM " + ClosedOrderRepository.TABLE;

        around()
                .withSQL(selectOrders)
                .run(() -> orderService.syncOrders());
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
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_NP_TEMPLATE)
        private NamedParameterJdbcOperations adminNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_JDBC_OPS)
        private JdbcOperations adminJdbcOperations;

        @Bean
        public ClosedOrderRepository closedOrderRepository() {
            return new ClosedOrderRepositoryImpl(adminNPJdbcOperations, adminJdbcOperations);
        }

        @Bean
        public ExchangeRatesService testExchangeRatesService() {
            return Mockito.mock(ExchangeRatesService.class);
        }

        @Bean
        public CoreOrderRepository coreOrderRepository() {
            return new CoreOrderRepositoryImpl(coreNPJdbcOperations);
        }

        @Bean
        public OrderService orderService() {
            return new OrderServiceImpl(closedOrderRepository(), testExchangeRatesService(), coreOrderRepository());
        }

        @Override
        protected String getSchema() {
            return "OrderServiceTest";
        }
    }
}
