package me.exrates.adminservice.core.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.repository.CoreExorderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreExorderRepositoryImplTest.InnerConfig.class
})
public class CoreExorderRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
    private NamedParameterJdbcOperations coreNPJdbcOperations;

    @Autowired
    private CoreExorderRepository coreExorderRepository;

    private CoreExorderRepositoryImpl impl = new CoreExorderRepositoryImpl(coreNPJdbcOperations);

    @Test
    public void testGetDailyBuySellVolume() {
        final Map<String, Integer> result = coreExorderRepository.getDailyBuySellVolume();

        assertEquals(11, (int)result.get("buy"));
        assertEquals(89, (int)result.get("sell"));
    }

    @Test
    public void testGetUniqueUsers() {
        final int result = coreExorderRepository.getDailyUniqueUsersQuantity();
        assertEquals(3, result);
    }

    @Test
    public void getPercentage_bothGtZero() {
        Map<String, BigDecimal> values = new HashMap<>();
        values.put("buy", BigDecimal.valueOf(25));
        values.put("sell", BigDecimal.valueOf(75));
        final Map<String, Integer> result = impl.getPercentage(values);

        assertEquals(25, (int)result.get("buy"));
        assertEquals(75, (int)result.get("sell"));
    }

    @Test
    public void getPercentage_SellEqZero() {
        Map<String, BigDecimal> values = new HashMap<>();
        values.put("buy", BigDecimal.valueOf(100));
        values.put("sell", BigDecimal.ZERO);
        final Map<String, Integer> result = impl.getPercentage(values);

        assertEquals(100, (int)result.get("buy"));
        assertEquals(0, (int)result.get("sell"));
    }

    @Test
    public void getPercentage_BuyEqZero() {
        Map<String, BigDecimal> values = new HashMap<>();
        values.put("buy", BigDecimal.ZERO);
        values.put("sell", BigDecimal.valueOf(100));
        final Map<String, Integer> result = impl.getPercentage(values);

        assertEquals(0, (int)result.get("buy"));
        assertEquals(100, (int)result.get("sell"));
    }

    @Test
    public void getPercentage_BuyAbsent() {
        Map<String, BigDecimal> values = new HashMap<>();
        values.put("sell", BigDecimal.valueOf(100));
        final Map<String, Integer> result = impl.getPercentage(values);

        assertEquals(0, (int)result.get("buy"));
        assertEquals(100, (int)result.get("sell"));
    }

    @Test
    public void getPercentage_BothAbsent() {
        final Map<String, Integer> result = impl.getPercentage(Collections.emptyMap());

        assertEquals(0, (int)result.get("buy"));
        assertEquals(0, (int)result.get("sell"));
    }

    @Configuration
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Bean
        CoreExorderRepository coreExorderRepository() {
            return new CoreExorderRepositoryImpl(coreNPJdbcOperations);
        }

        @Override
        protected String getSchema() {
            return "CoreExorderRepositoryImplTest";
        }
    }
}
