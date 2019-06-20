package me.exrates.adminservice.repository.impl;

import config.AbstractDatabaseContextTest;
import config.DataComparisonTest;
import me.exrates.adminservice.domain.ExternalReservedWalletAddressDto;
import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.repository.WalletRepository;
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
@ContextConfiguration(classes = WalletRepositoryImplTest.InnerConfig.class)
public class WalletRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier("testWalletRepository")
    private WalletRepository walletRepository;

    @Override
    protected void before() {
        try {
            truncateTables(WalletRepository.TABLE_NAME_1, WalletRepository.TABLE_NAME_2, WalletRepository.TABLE_NAME_3);

            String sql1 = "INSERT INTO " + WalletRepository.TABLE_NAME_1
                    + " (currency_id, currency_name) VALUES (1, \'TEST_COIN\');";
            String sql2 = "INSERT INTO " + WalletRepository.TABLE_NAME_2
                    + " (currency_id, name, wallet_address) VALUES (1, \'wallet_name\', \'wallet_address\');";
            String sql3 = "INSERT INTO " + WalletRepository.TABLE_NAME_3
                    + " (currency_id, currency_name, role_id, role_name) VALUES (1, \'TEST_COIN\', 1, \'USER\');";
            prepareTestData(sql1, sql2, sql3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getExternalMainWalletBalances_ok() {
        List<ExternalWalletBalancesDto> result = walletRepository.getExternalMainWalletBalances();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getExternalMainWalletBalances_not_found() throws Exception {
        truncateTables(WalletRepository.TABLE_NAME_1);

        List<ExternalWalletBalancesDto> result = walletRepository.getExternalMainWalletBalances();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void getInternalWalletBalances_ok() {
        List<InternalWalletBalancesDto> result = walletRepository.getInternalWalletBalances();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getInternalWalletBalances_not_found() throws Exception {
        truncateTables(WalletRepository.TABLE_NAME_3);

        List<InternalWalletBalancesDto> result = walletRepository.getInternalWalletBalances();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void updateExternalMainWalletBalances_ok() {
        final List<ExternalWalletBalancesDto> list = Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("TEST_COIN")
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.ONE)
                .mainBalance(BigDecimal.TEN)
                .build());
        around()
                .withSQL("SELECT * FROM " + WalletRepository.TABLE_NAME_1)
                .run(() -> walletRepository.updateExternalMainWalletBalances(list));
    }

    @Test
    public void updateInternalWalletBalances_ok() {
        final List<InternalWalletBalancesDto> list = Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("TEST_COIN")
                .roleId(1)
                .roleName(UserRole.USER)
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.ONE)
                .totalBalance(BigDecimal.TEN)
                .build());
        around()
                .withSQL("SELECT * FROM " + WalletRepository.TABLE_NAME_3)
                .run(() -> walletRepository.updateInternalWalletBalances(list));
    }

    @Test
    public void updateExternalReservedWalletBalances_ok() {
        around()
                .withSQL("SELECT * FROM " + WalletRepository.TABLE_NAME_1, "SELECT * FROM " + WalletRepository.TABLE_NAME_2)
                .run(() -> walletRepository.updateExternalReservedWalletBalances(1, "wallet_address", BigDecimal.ONE, LocalDateTime.now()));
    }

    @Test
    public void createReservedWalletAddress_ok() {
        around()
                .withSQL("SELECT * FROM " + WalletRepository.TABLE_NAME_2)
                .run(() -> walletRepository.createReservedWalletAddress(1));
    }

    @Test
    public void deleteReservedWalletAddress_ok() {
        around()
                .withSQL("SELECT * FROM " + WalletRepository.TABLE_NAME_2)
                .run(() -> walletRepository.deleteReservedWalletAddress(1, 1));
    }

    @Test
    public void updateReservedWalletAddress_ok() {
        final ExternalReservedWalletAddressDto reservedWalletAddressDto = ExternalReservedWalletAddressDto.builder()
                .id(1)
                .currencyId(1)
                .name("wallet_name_2")
                .walletAddress("wallet_address")
                .balance(BigDecimal.TEN)
                .build();
        around()
                .withSQL("SELECT * FROM " + WalletRepository.TABLE_NAME_2)
                .run(() -> walletRepository.updateReservedWalletAddress(reservedWalletAddressDto));
    }

    @Test
    public void getReservedWalletsByCurrencyId_ok() {
        List<ExternalReservedWalletAddressDto> result = walletRepository.getReservedWalletsByCurrencyId("1");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void getReservedWalletsByCurrencyId_not_found() {
        List<ExternalReservedWalletAddressDto> result = walletRepository.getReservedWalletsByCurrencyId("2");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void retrieveSummaryUSD_ok() {
        BigDecimal result = walletRepository.retrieveSummaryUSD();

        assertNotNull(result);
    }

    @Test
    public void retrieveSummaryBTC_ok() {
        BigDecimal result = walletRepository.retrieveSummaryBTC();

        assertNotNull(result);
    }

    @Test
    public void updateAccountingImbalance_ok() {
        around()
                .withSQL("SELECT * FROM " + WalletRepository.TABLE_NAME_1)
                .run(() -> walletRepository.updateAccountingImbalance("TEST_COIN", BigDecimal.TEN, BigDecimal.ONE));
    }

    @Test
    public void updateSignOfCertaintyForCurrency_ok() {
        around()
                .withSQL("SELECT * FROM " + WalletRepository.TABLE_NAME_1)
                .run(() -> walletRepository.updateSignOfCertaintyForCurrency(1, Boolean.TRUE));
    }

    @Test
    public void updateSignOfMonitoringForCurrency_ok() {
        around()
                .withSQL("SELECT * FROM " + WalletRepository.TABLE_NAME_1)
                .run(() -> walletRepository.updateSignOfMonitoringForCurrency(1, Boolean.TRUE));
    }

    @Test
    public void updateMonitoringRangeForCurrency_ok() {
        around()
                .withSQL("SELECT * FROM " + WalletRepository.TABLE_NAME_1)
                .run(() -> walletRepository.updateMonitoringRangeForCurrency(1, BigDecimal.TEN, Boolean.TRUE, BigDecimal.ONE, Boolean.FALSE));
    }

    @Configuration
    static class InnerConfig extends AbstractDatabaseContextTest.AppContextConfig {

        @Autowired
        @Qualifier(TEST_ADMIN_NP_TEMPLATE)
        protected NamedParameterJdbcOperations adminNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_JDBC_OPS)
        protected JdbcOperations adminJdbcOperations;

        @Override
        protected String getSchema() {
            return "WalletRepositoryImplTest";
        }

        @Bean("testWalletRepository")
        public WalletRepository walletRepository() {
            return new WalletRepositoryImpl(adminNPJdbcOperations, adminJdbcOperations);
        }
    }
}