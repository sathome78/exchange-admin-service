package me.exrates.adminservice.core.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.repository.CoreCompanyWalletRepository;
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

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreCompanyWalletRepositoryImplTest.InnerConfig.class
})
public class CoreCompanyWalletRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier("testCoreCompanyWalletRepository")
    private CoreCompanyWalletRepository coreCompanyWalletRepository;

    @Override
    protected void before() {
        setDatabaseType(CORE_DATABASE_TYPE);
        try {
            String sql = "INSERT INTO " + CoreCompanyWalletRepository.TABLE_NAME
                    + " (id, currency_id, balance, commission_balance) VALUES (2, 5, 10.0, 10.0);";
            prepareTestData(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void after() {
        try {
            String sql = "DELETE FROM " + CoreCompanyWalletRepository.TABLE_NAME
                    + " WHERE id = 2;";
            prepareTestData(sql);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void findByCurrency_ok() {
        CoreCurrencyDto currencyDto = CoreCurrencyDto.builder()
                .id(4)
                .name("BTC")
                .hidden(false)
                .build();

        CoreCompanyWalletDto companyWalletDto = coreCompanyWalletRepository.findByCurrency(currencyDto);

        assertNotNull(companyWalletDto);
    }

    @Test
    public void findByCurrency_not_found() {
        CoreCurrencyDto currencyDto = CoreCurrencyDto.builder()
                .id(0)
                .name("TEST_COIN")
                .hidden(false)
                .build();

        CoreCompanyWalletDto companyWalletDto = coreCompanyWalletRepository.findByCurrency(currencyDto);

        assertNull(companyWalletDto);
    }

    @Test
    public void update_ok() {
        CoreCompanyWalletDto coreCompanyWalletDto = CoreCompanyWalletDto.builder()
                .id(2)
                .balance(BigDecimal.valueOf(20))
                .commissionBalance(BigDecimal.valueOf(20))
                .build();

        around()
                .withSQL("SELECT * FROM " + CoreCompanyWalletRepository.TABLE_NAME)
                .run(() -> coreCompanyWalletRepository.update(coreCompanyWalletDto));
    }

    @Configuration
    @Profile("test")
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Override
        protected String getSchema() {
            return "CoreCompanyWalletRepositoryImplTest";
        }

        @Bean("testCoreCompanyWalletRepository")
        CoreCompanyWalletRepository coreCompanyWalletRepository() {
            return new CoreCompanyWalletRepositoryImpl(coreNPJdbcOperations);
        }
    }
}