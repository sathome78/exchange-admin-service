package me.exrates.adminservice.core.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.CoreWalletDto;
import me.exrates.adminservice.core.domain.CoreWalletOperationDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.TransactionSourceType;
import me.exrates.adminservice.core.domain.enums.WalletTransferStatus;
import me.exrates.adminservice.core.repository.CoreTransactionRepository;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.repository.CoreWalletRepository;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreWalletRepositoryImplTest.InnerConfig.class
})
public class CoreWalletRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier("testCoreWalletRepository")
    private CoreWalletRepository coreWalletRepository;

    @Override
    protected void before() {
        setDatabaseType(CORE_DATABASE_TYPE);
    }

    @Test
    public void getWalletBalances_ok() {
        List<InternalWalletBalancesDto> walletBalances = coreWalletRepository.getWalletBalances();

        assertNotNull(walletBalances);
        assertFalse(walletBalances.isEmpty());
        assertEquals(4, walletBalances.size());
    }

    @Test
    public void findByUserAndCurrency_ok() {
        CoreWalletDto wallet = coreWalletRepository.findByUserAndCurrency(1, 2);

        assertNotNull(wallet);
        assertEquals(1, wallet.getUserId());
        assertEquals(2, wallet.getCurrencyId());
    }

    @Test
    public void findByUserAndCurrency_not_found() {
        CoreWalletDto wallet = coreWalletRepository.findByUserAndCurrency(2, 2);

        assertNull(wallet);
    }

    @Test
    public void isUserAllowedToManuallyChangeWalletBalance_allowed() {
        boolean allowed = coreWalletRepository.isUserAllowedToManuallyChangeWalletBalance(1, 2);

        assertTrue(allowed);
    }

    @Test
    public void isUserAllowedToManuallyChangeWalletBalance_not_allowed() {
        boolean allowed = coreWalletRepository.isUserAllowedToManuallyChangeWalletBalance(2, 1);

        assertFalse(allowed);
    }

    @Test
    public void walletBalanceChange_ok() {
        CoreWalletOperationDto walletOperationDto = CoreWalletOperationDto.builder()
                .amount(BigDecimal.TEN)
                .operationType(OperationType.INPUT)
                .walletId(1)
                .balanceType(CoreWalletOperationDto.BalanceType.ACTIVE)
                .commission(CoreCommissionDto.builder()
                        .id(1)
                        .build())
                .commissionAmount(BigDecimal.ONE)
                .sourceType(TransactionSourceType.MANUAL)
                .build();

        WalletTransferStatus status = coreWalletRepository.walletBalanceChange(walletOperationDto);

        assertNotNull(status);
        assertEquals(WalletTransferStatus.SUCCESS, status);
    }

    @Test
    public void walletBalanceChange_wallet_not_found() {
        CoreWalletOperationDto walletOperationDto = CoreWalletOperationDto.builder()
                .amount(BigDecimal.TEN)
                .operationType(OperationType.INPUT)
                .walletId(0)
                .balanceType(CoreWalletOperationDto.BalanceType.ACTIVE)
                .commission(CoreCommissionDto.builder()
                        .id(1)
                        .build())
                .commissionAmount(BigDecimal.ONE)
                .sourceType(TransactionSourceType.MANUAL)
                .build();

        WalletTransferStatus status = coreWalletRepository.walletBalanceChange(walletOperationDto);

        assertNotNull(status);
        assertEquals(WalletTransferStatus.WALLET_NOT_FOUND, status);
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
            return "CoreWalletRepositoryImplTest";
        }

        @Bean("testCoreTransactionRepository")
        CoreTransactionRepository coreTransactionRepository() {
            return new CoreTransactionRepositoryImpl(coreNPJdbcOperations, coreWalletRepository());
        }

        @Bean
        CoreUserRepository coreUserRepository() {
            return new CoreUserRepositoryImpl(coreNPJdbcOperations, coreJdbcOperations);
        }

        @Bean("testCoreWalletRepository")
        CoreWalletRepository coreWalletRepository() {
            return new CoreWalletRepositoryImpl(coreUserRepository(), coreTransactionRepository(), coreNPJdbcOperations);
        }
    }
}
