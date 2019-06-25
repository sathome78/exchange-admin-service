package me.exrates.adminservice.core.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.domain.CoreTransactionDto;
import me.exrates.adminservice.core.domain.CoreWalletDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.TransactionSourceType;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.repository.CoreWalletRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreTransactionRepositoryImplTest.InnerConfig.class
})
public class CoreTransactionRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier("testCoreTransactionRepository")
    private CoreTransactionRepository coreTransactionRepository;

    @Override
    protected void before() {
        setDatabaseType(CORE_DATABASE_TYPE);
    }

    @Test
    public void findAllLimited_ok() {
        List<CoreTransaction> allLimited = coreTransactionRepository.findAllLimited(15, 0);

        assertNotNull(allLimited);
        assertFalse(allLimited.isEmpty());
        assertEquals(15, allLimited.size());
    }

    @Test
    public void findAllLimited_not_found() {
        List<CoreTransaction> allLimited = coreTransactionRepository.findAllLimited(15, 20);

        assertNotNull(allLimited);
        assertTrue(allLimited.isEmpty());
    }

    @Test
    public void create_ok() throws Exception {
        CoreTransactionDto transactionDto = CoreTransactionDto.builder()
                .userWallet(CoreWalletDto.builder().id(1).build())
                .companyWallet(CoreCompanyWalletDto.builder().id(1).build())
                .amount(BigDecimal.TEN)
                .commissionAmount(BigDecimal.ONE)
                .commission(CoreCommissionDto.builder().id(1).build())
                .operationType(OperationType.INPUT)
                .currency(CoreCurrencyDto.builder().id(4).build())
                .build();

        AtomicReference<CoreTransactionDto> updatedTransactionDto = new AtomicReference<>();
        around()
                .withSQL("SELECT * FROM " + CoreTransactionRepository.TABLE_NAME)
                .run(() -> {
                    updatedTransactionDto.set(coreTransactionRepository.create(transactionDto));
                });

        String sql = "DELETE FROM " + CoreTransactionRepository.TABLE_NAME
                + " WHERE id = " + updatedTransactionDto.get().getId();
        prepareTestData(sql);
    }

    @Test
    public void updateForProvided_ok() throws Exception {
        CoreTransactionDto transactionDto = CoreTransactionDto.builder()
                .id(1)
                .activeBalanceBefore(BigDecimal.ONE)
                .reservedBalanceBefore(BigDecimal.ONE)
                .companyBalanceBefore(BigDecimal.ONE)
                .companyCommissionBalanceBefore(BigDecimal.ONE)
                .sourceType(TransactionSourceType.REFILL)
                .sourceId(1)
                .build();

        around()
                .withSQL("SELECT * FROM " + CoreTransactionRepository.TABLE_NAME)
                .run(() -> coreTransactionRepository.updateForProvided(transactionDto));

        String sql = "UPDATE " + CoreTransactionRepository.TABLE_NAME
                + " SET active_balance_before = null, reserved_balance_before = null, company_balance_before = null, company_commission_balance_before = null, provided_modification_date = null, provided = 0 WHERE id = 1";
        prepareTestData(sql);
    }

    @Configuration
    @Profile("test")
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_CORE_TEMPLATE) // it's ok bean will be imported later
        private JdbcOperations coreJdbcOperations;

        @Override
        protected String getSchema() {
            return "CoreTransactionRepositoryImplTest";
        }

        @Bean
        CoreUserRepository coreUserRepository() {
            return new CoreUserRepositoryImpl(coreNPJdbcOperations, coreJdbcOperations);
        }

        @Bean
        CoreWalletRepository coreWalletRepository() {
            return new CoreWalletRepositoryImpl(coreUserRepository(), coreNPJdbcOperations, coreTransactionRepository());
        }

        @Bean("testCoreTransactionRepository")
        CoreTransactionRepository coreTransactionRepository() {
            return new CoreTransactionRepositoryImpl(coreNPJdbcOperations, coreUserRepository());
        }
    }
}
