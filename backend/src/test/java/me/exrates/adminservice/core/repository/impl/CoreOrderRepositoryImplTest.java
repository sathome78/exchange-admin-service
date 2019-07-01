package me.exrates.adminservice.core.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreOrderDto;
import me.exrates.adminservice.core.repository.CoreOrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreOrderRepositoryImplTest.InnerConfig.class
})
public class CoreOrderRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
    private NamedParameterJdbcOperations coreNPJdbcOperations;

    @Autowired
    @Qualifier("testCoreOrderRepository")
    private CoreOrderRepository coreOrderRepository;

    private CoreOrderRepositoryImpl impl = new CoreOrderRepositoryImpl(coreNPJdbcOperations);

    @Test
    public void testGetDailyBuySellVolume() {
        final Map<String, Integer> result = coreOrderRepository.getDailyBuySellVolume();

        assertEquals(11, (int) result.get("buy"));
        assertEquals(89, (int) result.get("sell"));
    }

    @Test
    public void testGetUniqueUsers() {
        final int result = coreOrderRepository.getDailyUniqueUsersQuantity();
        assertEquals(3, result);
    }

    @Test
    public void getPercentage_bothGtZero() {
        Map<String, BigDecimal> values = new HashMap<>();
        values.put("buy", BigDecimal.valueOf(25));
        values.put("sell", BigDecimal.valueOf(75));
        final Map<String, Integer> result = impl.getPercentage(values);

        assertEquals(25, (int) result.get("buy"));
        assertEquals(75, (int) result.get("sell"));
    }

    @Test
    public void getPercentage_SellEqZero() {
        Map<String, BigDecimal> values = new HashMap<>();
        values.put("buy", BigDecimal.valueOf(100));
        values.put("sell", BigDecimal.ZERO);
        final Map<String, Integer> result = impl.getPercentage(values);

        assertEquals(100, (int) result.get("buy"));
        assertEquals(0, (int) result.get("sell"));
    }

    @Test
    public void getPercentage_BuyEqZero() {
        Map<String, BigDecimal> values = new HashMap<>();
        values.put("buy", BigDecimal.ZERO);
        values.put("sell", BigDecimal.valueOf(100));
        final Map<String, Integer> result = impl.getPercentage(values);

        assertEquals(0, (int) result.get("buy"));
        assertEquals(100, (int) result.get("sell"));
    }

    @Test
    public void getPercentage_BuyAbsent() {
        Map<String, BigDecimal> values = new HashMap<>();
        values.put("sell", BigDecimal.valueOf(100));
        final Map<String, Integer> result = impl.getPercentage(values);

        assertEquals(0, (int) result.get("buy"));
        assertEquals(100, (int) result.get("sell"));
    }

    @Test
    public void getPercentage_BothAbsent() {
        final Map<String, Integer> result = impl.getPercentage(Collections.emptyMap());

        assertEquals(0, (int) result.get("buy"));
        assertEquals(0, (int) result.get("sell"));
    }

    @Test
    public void findOrderById_ok() {
        CoreOrderDto order = coreOrderRepository.findOrderById(1);

        assertNotNull(order);
        assertEquals(1, order.getId());
    }

    @Test
    public void findOrderById_not_found() {
        CoreOrderDto order = coreOrderRepository.findOrderById(0);

        assertNull(order);
    }

    @Configuration
    @Profile("test")
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Override
        protected String getSchema() {
            return "CoreExorderRepositoryImplTest";
        }

        @Bean("testCoreOrderRepository")
        CoreOrderRepository coreOrderRepository() {
            return new CoreOrderRepositoryImpl(coreNPJdbcOperations);
        }
    }
}