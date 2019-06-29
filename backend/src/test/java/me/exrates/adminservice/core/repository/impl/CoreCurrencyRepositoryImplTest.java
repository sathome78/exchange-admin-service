package me.exrates.adminservice.core.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.repository.CoreCurrencyRepository;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreCurrencyRepositoryImplTest.InnerConfig.class
})
public class CoreCurrencyRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier("testCoreCurrencyRepository")
    private CoreCurrencyRepository coreCurrencyRepository;

    @Override
    protected void before() {
        setDatabaseType(CORE_DATABASE_TYPE);
    }

    @Test
    public void findById_ok() {
        CoreCurrencyDto currencyDto = coreCurrencyRepository.findCurrencyById(4);

        assertNotNull(currencyDto);
        assertEquals(4, currencyDto.getId());
    }

    @Test
    public void findById_not_found() {
        CoreCurrencyDto currencyDto = coreCurrencyRepository.findCurrencyById(0);

        assertNull(currencyDto);
    }

    @Test
    public void findByName_ok() {
        CoreCurrencyDto currencyDto = coreCurrencyRepository.findCurrencyByName("BTC");

        assertNotNull(currencyDto);
        assertEquals("BTC", currencyDto.getName());
    }

    @Test
    public void findByName_not_found() {
        CoreCurrencyDto currencyDto = coreCurrencyRepository.findCurrencyByName("TEST_COIN");

        assertNull(currencyDto);
    }

    @Test
    public void getAllCurrencies_ok() {
        List<CoreCurrencyDto> currencies = coreCurrencyRepository.getAllCurrencies();

        assertNotNull(currencies);
        assertFalse(currencies.isEmpty());
        assertEquals(5, currencies.size());
    }

    @Test
    public void getActiveCurrencies_ok() {
        List<CoreCurrencyDto> activeCurrencies = coreCurrencyRepository.getActiveCurrencies();

        assertNotNull(activeCurrencies);
        assertFalse(activeCurrencies.isEmpty());
        assertEquals(4, activeCurrencies.size());
    }

    @Test
    public void getCurrencyName_ok() {
        String currencyName = coreCurrencyRepository.getCurrencyName(4);

        assertNotNull(currencyName);
        assertEquals("BTC", currencyName);
    }

    @Test
    public void getCurrencyName_not_found() {
        String currencyName = coreCurrencyRepository.getCurrencyName(0);

        assertNull(currencyName);
    }

    @Configuration
    @Profile("test")
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Override
        protected String getSchema() {
            return "CoreCurrencyRepositoryImplTest";
        }

        @Bean("testCoreCurrencyRepository")
        CoreCurrencyRepository coreCurrencyRepository() {
            return new CoreCurrencyRepositoryImpl(coreNPJdbcOperations);
        }
    }
}