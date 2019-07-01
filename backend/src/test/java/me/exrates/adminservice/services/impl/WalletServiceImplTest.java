package me.exrates.adminservice.services.impl;

import me.exrates.adminservice.api.WalletsApi;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.repository.CoreWalletRepository;
import me.exrates.adminservice.domain.BalancesDto;
import me.exrates.adminservice.domain.DashboardOneDto;
import me.exrates.adminservice.domain.DashboardTwoDto;
import me.exrates.adminservice.domain.ExternalReservedWalletAddressDto;
import me.exrates.adminservice.domain.ExternalWalletBalancesDto;
import me.exrates.adminservice.domain.InternalWalletBalancesDto;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.BalanceDto;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.repository.WalletRepository;
import me.exrates.adminservice.core.service.CoreCurrencyService;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.services.WalletBalancesService;
import me.exrates.adminservice.services.WalletService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class WalletServiceImplTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private CoreWalletRepository coreWalletRepository;
    @Mock
    private ExchangeRatesService exchangeRatesService;
    @Mock
    private WalletBalancesService walletBalancesService;
    @Mock
    private CoreCurrencyService coreCurrencyService;
    @Mock
    private WalletsApi walletsApi;

    private WalletService walletService;

    @Before
    public void setUp() throws Exception {
        walletService = spy(new WalletServiceImpl(
                walletRepository,
                coreWalletRepository,
                exchangeRatesService,
                walletBalancesService,
                coreCurrencyService,
                walletsApi));
    }

    @Test
    public void getExternalWalletBalances_balances_list_is_empty() {
        doReturn(Collections.emptyList())
                .when(walletRepository)
                .getExternalMainWalletBalances();

        PagedResult<ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances(1, 0);

        assertNotNull(externalWalletBalances);
        assertEquals(0, externalWalletBalances.getCount());
        assertTrue(externalWalletBalances.getItems().isEmpty());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
    }

    @Test
    public void getExternalWalletBalances_balances_list_is_not_empty() {
        doReturn(Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .build()))
                .when(walletRepository)
                .getExternalMainWalletBalances();
        doReturn(Collections.singletonList(CoreCurrencyDto.builder()
                .name("BTC")
                .build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        PagedResult<ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances(1, 0);

        assertNotNull(externalWalletBalances);
        assertEquals(1, externalWalletBalances.getCount());
        assertNotNull(externalWalletBalances.getItems());
        assertFalse(externalWalletBalances.getItems().isEmpty());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
    }

    @Test
    public void getExternalWalletBalances_balances_limit_one() {
        List<ExternalWalletBalancesDto> externalWalletBalancesList = new ArrayList<>();
        externalWalletBalancesList.add(ExternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .build());
        externalWalletBalancesList.add(ExternalWalletBalancesDto.builder()
                .currencyName("ETH")
                .build());
        doReturn(externalWalletBalancesList)
                .when(walletRepository)
                .getExternalMainWalletBalances();

        List<CoreCurrencyDto> coreCurrencyList = new ArrayList<>();
        coreCurrencyList.add(CoreCurrencyDto.builder()
                .name("BTC")
                .build());
        coreCurrencyList.add(CoreCurrencyDto.builder()
                .name("ETH")
                .build());
        doReturn(coreCurrencyList)
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        PagedResult<ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances(1, 0);

        assertNotNull(externalWalletBalances);
        assertEquals(2, externalWalletBalances.getCount());
        assertNotNull(externalWalletBalances.getItems());
        assertFalse(externalWalletBalances.getItems().isEmpty());
        assertEquals(1, externalWalletBalances.getItems().size());
        assertEquals("BTC", externalWalletBalances.getItems().get(0).getCurrencyName());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
    }

    @Test
    public void getExternalWalletBalances_balances_offset_one() {
        List<ExternalWalletBalancesDto> externalWalletBalancesList = new ArrayList<>();
        externalWalletBalancesList.add(ExternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .build());
        externalWalletBalancesList.add(ExternalWalletBalancesDto.builder()
                .currencyName("ETH")
                .build());
        doReturn(externalWalletBalancesList)
                .when(walletRepository)
                .getExternalMainWalletBalances();

        List<CoreCurrencyDto> coreCurrencyList = new ArrayList<>();
        coreCurrencyList.add(CoreCurrencyDto.builder()
                .name("BTC")
                .build());
        coreCurrencyList.add(CoreCurrencyDto.builder()
                .name("ETH")
                .build());
        doReturn(coreCurrencyList)
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        PagedResult<ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances(2, 1);

        assertNotNull(externalWalletBalances);
        assertEquals(2, externalWalletBalances.getCount());
        assertNotNull(externalWalletBalances.getItems());
        assertFalse(externalWalletBalances.getItems().isEmpty());
        assertEquals(1, externalWalletBalances.getItems().size());
        assertEquals("ETH", externalWalletBalances.getItems().get(0).getCurrencyName());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
    }

    @Test
    public void getDashboardOne_success() {
        doReturn(Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .totalBalanceUSD(BigDecimal.TEN)
                .build()))
                .when(walletRepository)
                .getExternalMainWalletBalances();

        List<InternalWalletBalancesDto> internalWalletBalancesList = new ArrayList<>();
        internalWalletBalancesList.add(InternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .roleName(UserRole.ACCOUNTANT)
                .totalBalanceUSD(BigDecimal.ZERO)
                .build());
        internalWalletBalancesList.add(InternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .roleName(UserRole.USER)
                .totalBalanceUSD(BigDecimal.TEN)
                .build());
        doReturn(internalWalletBalancesList)
                .when(walletRepository)
                .getInternalWalletBalances();

        doReturn(Collections.singletonList(CoreCurrencyDto.builder()
                .name("BTC")
                .build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        doReturn(Collections.singletonList(CoreCurrencyDto.builder()
                .name("BTC")
                .build()))
                .when(coreCurrencyService)
                .getCachedCurrencies();

        DashboardOneDto dashboardOne = walletService.getDashboardOne();

        assertNotNull(dashboardOne);
        assertEquals(0, BigDecimal.TEN.compareTo(dashboardOne.getExWalletBalancesUSDSum()));
        assertEquals(0, BigDecimal.TEN.compareTo(dashboardOne.getInWalletBalancesUSDSum()));
        assertEquals(0, BigDecimal.ZERO.compareTo(dashboardOne.getDeviationUSD()));
        assertEquals(1, dashboardOne.getActiveCurrenciesCount());
        assertEquals(1, dashboardOne.getAllCurrenciesCount());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
        verify(coreCurrencyService, atLeastOnce()).getCachedCurrencies();
    }

    @Test
    public void getExternalWalletBalances__list_is_empty() {
        doReturn(Collections.emptyList())
                .when(walletRepository)
                .getExternalMainWalletBalances();

        List<ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances();

        assertNotNull(externalWalletBalances);
        assertTrue(externalWalletBalances.isEmpty());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
    }

    @Test
    public void getExternalWalletBalances__currency_map_is_empty() {
        doReturn(Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .build()))
                .when(walletRepository)
                .getExternalMainWalletBalances();
        doReturn(Collections.emptyList())
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        List<ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances();

        assertNotNull(externalWalletBalances);
        assertTrue(externalWalletBalances.isEmpty());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
    }

    @Test
    public void getExternalWalletBalances__list_is_not_empty() {
        doReturn(Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .build()))
                .when(walletRepository)
                .getExternalMainWalletBalances();
        doReturn(Collections.singletonList(CoreCurrencyDto.builder()
                .name("BTC")
                .build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        List<ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances();

        assertNotNull(externalWalletBalances);
        assertFalse(externalWalletBalances.isEmpty());
        assertEquals(1, externalWalletBalances.size());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
    }

    @Test
    public void getInternalWalletBalances__list_is_empty() {
        doReturn(Collections.emptyList())
                .when(walletRepository)
                .getInternalWalletBalances();

        List<InternalWalletBalancesDto> internalWalletBalances = walletService.getInternalWalletBalances();

        assertNotNull(internalWalletBalances);
        assertTrue(internalWalletBalances.isEmpty());

        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
    }

    @Test
    public void getInternalWalletBalances__currency_map_is_empty() {
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .roleName(UserRole.USER)
                .build()))
                .when(walletRepository)
                .getInternalWalletBalances();
        doReturn(Collections.emptyList())
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        List<InternalWalletBalancesDto> internalWalletBalances = walletService.getInternalWalletBalances();

        assertNotNull(internalWalletBalances);
        assertTrue(internalWalletBalances.isEmpty());

        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
    }

    @Test
    public void getInternalWalletBalances__list_is_not_empty() {
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .roleName(UserRole.USER)
                .build()))
                .when(walletRepository)
                .getInternalWalletBalances();
        doReturn(Collections.singletonList(CoreCurrencyDto.builder()
                .name("BTC")
                .build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        List<InternalWalletBalancesDto> internalWalletBalances = walletService.getInternalWalletBalances();

        assertNotNull(internalWalletBalances);
        assertFalse(internalWalletBalances.isEmpty());
        assertEquals(1, internalWalletBalances.size());

        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
    }

    @Test
    public void getWalletBalances_list_is_empty() {
        doReturn(Collections.emptyList())
                .when(coreWalletRepository)
                .getWalletBalances();

        List<InternalWalletBalancesDto> walletBalances = walletService.getWalletBalances();

        assertNotNull(walletBalances);
        assertTrue(walletBalances.isEmpty());

        verify(coreWalletRepository, atLeastOnce()).getWalletBalances();
    }

    @Test
    public void getWalletBalances_list_is_not_empty() {
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder().build()))
                .when(coreWalletRepository)
                .getWalletBalances();

        List<InternalWalletBalancesDto> walletBalances = walletService.getWalletBalances();

        assertNotNull(walletBalances);
        assertFalse(walletBalances.isEmpty());
        assertEquals(1, walletBalances.size());

        verify(coreWalletRepository, atLeastOnce()).getWalletBalances();
    }

    @Test
    public void updateExternalMainWalletBalances_balances_list_is_empty() {
        doReturn(Collections.singletonMap("BTC", RateDto.builder().build()))
                .when(exchangeRatesService)
                .getCachedRates();
        doReturn(Collections.singletonMap("BTC", BalanceDto.builder().build()))
                .when(walletBalancesService)
                .getCachedBalances();
        doReturn(Collections.emptyList())
                .when(walletRepository)
                .getExternalMainWalletBalances();

        walletService.updateExternalMainWalletBalances();

        verify(exchangeRatesService, atLeastOnce()).getCachedRates();
        verify(walletBalancesService, atLeastOnce()).getCachedBalances();
        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(walletRepository, never()).updateExternalMainWalletBalances(anyList());
    }

    @Test
    public void updateExternalMainWalletBalances_success() {
        doReturn(Collections.singletonMap("BTC", RateDto.builder()
                .currencyName("BTC")
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.ONE)
                .build()))
                .when(exchangeRatesService)
                .getCachedRates();
        doReturn(Collections.singletonMap("BTC", BalanceDto.builder()
                .currencyName("BTC")
                .balance(BigDecimal.TEN)
                .lastUpdatedAt(LocalDateTime.now())
                .build()))
                .when(walletBalancesService)
                .getCachedBalances();
        doReturn(Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .build()))
                .when(walletRepository)
                .getExternalMainWalletBalances();
        doReturn(Collections.singletonList(CoreCurrencyDto.builder()
                .name("BTC")
                .build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();
        doNothing()
                .when(walletRepository)
                .updateExternalMainWalletBalances(anyList());

        walletService.updateExternalMainWalletBalances();

        verify(exchangeRatesService, atLeastOnce()).getCachedRates();
        verify(walletBalancesService, atLeastOnce()).getCachedBalances();
        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
        verify(walletRepository, atLeastOnce()).updateExternalMainWalletBalances(anyList());
    }

    @Test
    public void updateExternalReservedWalletBalances_reserved_balances_map_is_empty() {
        doReturn(Collections.emptyMap())
                .when(walletsApi)
                .getReservedBalancesFromApi();

        walletService.updateExternalReservedWalletBalances();

        verify(walletsApi, atLeastOnce()).getReservedBalancesFromApi();
        verify(coreCurrencyService, never()).findCachedCurrencyByName(anyString());
        verify(walletRepository, never()).updateExternalReservedWalletBalances(anyInt(), anyString(), any(BigDecimal.class), any(LocalDateTime.class));
    }

    @Test
    public void updateExternalReservedWalletBalances_currency_is_not_present() {
        doReturn(Collections.singletonMap("BTC||wallet_address||wallet_contract||" + FORMATTER.format(LocalDateTime.now()), BigDecimal.ONE))
                .when(walletsApi)
                .getReservedBalancesFromApi();
        doReturn(null)
                .when(coreCurrencyService)
                .findCachedCurrencyByName("BTC");

        walletService.updateExternalReservedWalletBalances();

        verify(walletsApi, atLeastOnce()).getReservedBalancesFromApi();
        verify(coreCurrencyService, atLeastOnce()).findCachedCurrencyByName(anyString());
        verify(walletRepository, never()).updateExternalReservedWalletBalances(anyInt(), anyString(), any(BigDecimal.class), any(LocalDateTime.class));
    }

    @Test
    public void updateExternalReservedWalletBalances_success() {
        doReturn(Collections.singletonMap("BTC||wallet_address||wallet_contract||" + FORMATTER.format(LocalDateTime.now()), BigDecimal.ONE))
                .when(walletsApi)
                .getReservedBalancesFromApi();
        doReturn(CoreCurrencyDto.builder()
                .id(1)
                .name("BTC")
                .build())
                .when(coreCurrencyService)
                .findCachedCurrencyByName("BTC");

        walletService.updateExternalReservedWalletBalances();

        verify(walletsApi, atLeastOnce()).getReservedBalancesFromApi();
        verify(coreCurrencyService, atLeastOnce()).findCachedCurrencyByName(anyString());
        verify(walletRepository, atLeastOnce()).updateExternalReservedWalletBalances(anyInt(), anyString(), any(BigDecimal.class), any(LocalDateTime.class));
    }

    @Test
    public void updateInternalWalletBalances_balances_list_is_empty() {
        doReturn(Collections.singletonMap("BTC", RateDto.builder().build()))
                .when(exchangeRatesService)
                .getCachedRates();
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .roleName(UserRole.USER)
                .build()))
                .when(coreWalletRepository)
                .getWalletBalances();
        doReturn(Collections.emptyList())
                .when(walletRepository)
                .getInternalWalletBalances();

        walletService.updateInternalWalletBalances();

        verify(exchangeRatesService, atLeastOnce()).getCachedRates();
        verify(coreWalletRepository, atLeastOnce()).getWalletBalances();
        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
        verify(walletRepository, never()).updateInternalWalletBalances(anyList());
    }

    @Test
    public void updateInternalWalletBalances_success() {
        doReturn(Collections.singletonMap("BTC", RateDto.builder()
                .currencyName("BTC")
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.ONE)
                .build()))
                .when(exchangeRatesService)
                .getCachedRates();
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyName("BTC")
                .roleName(UserRole.USER)
                .totalBalance(BigDecimal.TEN)
                .build()))
                .when(coreWalletRepository)
                .getWalletBalances();
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .roleId(1)
                .roleName(UserRole.USER)
                .build()))
                .when(walletRepository)
                .getInternalWalletBalances();
        doReturn(Collections.singletonList(CoreCurrencyDto.builder()
                .name("BTC")
                .build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();
        doNothing()
                .when(walletRepository)
                .updateInternalWalletBalances(anyList());

        walletService.updateInternalWalletBalances();

        verify(exchangeRatesService, atLeastOnce()).getCachedRates();
        verify(coreWalletRepository, atLeastOnce()).getWalletBalances();
        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
        verify(walletRepository, atLeastOnce()).updateInternalWalletBalances(anyList());
    }

    @Test
    public void createWalletAddress_success() {
        doNothing()
                .when(walletRepository)
                .createReservedWalletAddress(anyInt());

        walletService.createWalletAddress(1);

        verify(walletRepository, atLeastOnce()).createReservedWalletAddress(anyInt());
    }

    @Test
    public void deleteWalletAddress_currency_is_not_present() {
        doNothing()
                .when(walletRepository)
                .deleteReservedWalletAddress(anyInt(), anyInt());
        doReturn(null)
                .when(coreCurrencyService)
                .getCurrencyName(anyInt());

        walletService.deleteWalletAddress(1, 1, "wallet_address");

        verify(walletRepository, atLeastOnce()).deleteReservedWalletAddress(anyInt(), anyInt());
        verify(coreCurrencyService, atLeastOnce()).getCurrencyName(anyInt());
        verify(walletsApi, never()).deleteReservedWallet(anyString(), anyString());
    }

    @Test
    public void deleteWalletAddress_success() {
        doNothing()
                .when(walletRepository)
                .deleteReservedWalletAddress(anyInt(), anyInt());
        doReturn("BTC")
                .when(coreCurrencyService)
                .getCurrencyName(anyInt());
        doReturn(true)
                .when(walletsApi)
                .deleteReservedWallet(anyString(), anyString());

        walletService.deleteWalletAddress(1, 1, "wallet_address");

        verify(walletRepository, atLeastOnce()).deleteReservedWalletAddress(anyInt(), anyInt());
        verify(coreCurrencyService, atLeastOnce()).getCurrencyName(anyInt());
        verify(walletsApi, atLeastOnce()).deleteReservedWallet(anyString(), anyString());
    }

    @Test
    public void updateWalletAddress_isSaveAsAddress_false() {
        doNothing()
                .when(walletRepository)
                .updateReservedWalletAddress(any(ExternalReservedWalletAddressDto.class));

        walletService.updateWalletAddress(ExternalReservedWalletAddressDto.builder().build(), false);

        verify(walletRepository, atLeastOnce()).updateReservedWalletAddress(any(ExternalReservedWalletAddressDto.class));
        verify(coreCurrencyService, never()).getCurrencyName(anyInt());
        verify(walletsApi, never()).addReservedWallet(anyString(), anyString());
    }

    @Test
    public void updateWalletAddress_isSaveAsAddress_true_and_currency_is_not_present() {
        doNothing()
                .when(walletRepository)
                .updateReservedWalletAddress(any(ExternalReservedWalletAddressDto.class));
        doReturn(null)
                .when(coreCurrencyService)
                .getCurrencyName(anyInt());

        walletService.updateWalletAddress(ExternalReservedWalletAddressDto.builder().currencyId(1).build(), true);

        verify(walletRepository, atLeastOnce()).updateReservedWalletAddress(any(ExternalReservedWalletAddressDto.class));
        verify(coreCurrencyService, atLeastOnce()).getCurrencyName(anyInt());
        verify(walletsApi, never()).addReservedWallet(anyString(), anyString());
    }

    @Test
    public void updateWalletAddress_isSaveAsAddress_true_and_currency_is_present() {
        doNothing()
                .when(walletRepository)
                .updateReservedWalletAddress(any(ExternalReservedWalletAddressDto.class));
        doReturn("BTC")
                .when(coreCurrencyService)
                .getCurrencyName(anyInt());
        doReturn(true)
                .when(walletsApi)
                .addReservedWallet(anyString(), anyString());

        walletService.updateWalletAddress(ExternalReservedWalletAddressDto.builder().currencyId(1).walletAddress("wallets_address").build(), true);

        verify(walletRepository, atLeastOnce()).updateReservedWalletAddress(any(ExternalReservedWalletAddressDto.class));
        verify(coreCurrencyService, atLeastOnce()).getCurrencyName(anyInt());
        verify(walletsApi, atLeastOnce()).addReservedWallet(anyString(), anyString());
    }

    @Test
    public void getReservedWalletsByCurrencyId_success() {
        doReturn(Collections.singletonList(ExternalReservedWalletAddressDto.builder().build()))
                .when(walletRepository)
                .getReservedWalletsByCurrencyId(anyString());

        List<ExternalReservedWalletAddressDto> reservedWalletsList = walletService.getReservedWalletsByCurrencyId("1");

        assertNotNull(reservedWalletsList);
        assertFalse(reservedWalletsList.isEmpty());
        assertEquals(1, reservedWalletsList.size());

        verify(walletRepository, atLeastOnce()).getReservedWalletsByCurrencyId(anyString());
    }

    @Test
    public void getExternalReservedWalletBalance_currency_is_not_present() {
        doReturn(null)
                .when(coreCurrencyService)
                .findCachedCurrencyById(anyInt());

        BigDecimal balance = walletService.getExternalReservedWalletBalance(1, "wallet_address");

        assertNull(balance);

        verify(coreCurrencyService, atLeastOnce()).findCachedCurrencyById(anyInt());
        verify(walletsApi, never()).getBalanceByCurrencyAndWallet(anyString(), anyString());
    }

    @Test
    public void getExternalReservedWalletBalance_success() {
        doReturn(CoreCurrencyDto.builder()
                .name("BTC")
                .build())
                .when(coreCurrencyService)
                .findCachedCurrencyById(anyInt());
        doReturn(BigDecimal.TEN)
                .when(walletsApi)
                .getBalanceByCurrencyAndWallet(anyString(), anyString());

        BigDecimal balance = walletService.getExternalReservedWalletBalance(1, "wallet_address");

        assertNotNull(balance);
        assertEquals(0, BigDecimal.TEN.compareTo(balance));

        verify(coreCurrencyService, atLeastOnce()).findCachedCurrencyById(anyInt());
        verify(walletsApi, atLeastOnce()).getBalanceByCurrencyAndWallet(anyString(), anyString());
    }

    @Test
    public void retrieveSummaryUSD_success() {
        doReturn(BigDecimal.ONE)
                .when(walletRepository)
                .retrieveSummaryUSD();

        BigDecimal balance = walletService.retrieveSummaryUSD();

        assertNotNull(balance);
        assertEquals(0, BigDecimal.ONE.compareTo(balance));

        verify(walletRepository, atLeastOnce()).retrieveSummaryUSD();
    }

    @Test
    public void retrieveSummaryBTC_success() {
        doReturn(BigDecimal.ONE)
                .when(walletRepository)
                .retrieveSummaryBTC();

        BigDecimal balance = walletService.retrieveSummaryBTC();

        assertNotNull(balance);
        assertEquals(0, BigDecimal.ONE.compareTo(balance));

        verify(walletRepository, atLeastOnce()).retrieveSummaryBTC();
    }

    @Test
    public void updateAccountingImbalance_success() {
        doNothing()
                .when(walletRepository)
                .updateAccountingImbalance(anyString(), any(BigDecimal.class), any(BigDecimal.class));

        walletService.updateAccountingImbalance("BTC", BigDecimal.ONE, BigDecimal.ZERO);

        verify(walletRepository, atLeastOnce()).updateAccountingImbalance(anyString(), any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    public void updateSignOfCertaintyForCurrency_success() {
        doNothing()
                .when(walletRepository)
                .updateSignOfCertaintyForCurrency(anyInt(), anyBoolean());

        walletService.updateSignOfCertaintyForCurrency(1, true);

        verify(walletRepository, atLeastOnce()).updateSignOfCertaintyForCurrency(anyInt(), anyBoolean());
    }

    @Test
    public void updateSignOfMonitoringForCurrency_success() {
        doNothing()
                .when(walletRepository)
                .updateSignOfMonitoringForCurrency(anyInt(), anyBoolean());

        walletService.updateSignOfMonitoringForCurrency(1, true);

        verify(walletRepository, atLeastOnce()).updateSignOfMonitoringForCurrency(anyInt(), anyBoolean());
    }

    @Test
    public void updateMonitoringRangeForCurrency_success() {
        doNothing()
                .when(walletRepository)
                .updateMonitoringRangeForCurrency(anyInt(), any(BigDecimal.class), anyBoolean(), any(BigDecimal.class), anyBoolean());

        walletService.updateMonitoringRangeForCurrency(1, BigDecimal.ONE, true, BigDecimal.ZERO, false);

        verify(walletRepository, atLeastOnce()).updateMonitoringRangeForCurrency(anyInt(), any(BigDecimal.class), anyBoolean(), any(BigDecimal.class), anyBoolean());
    }

    @Test
    public void getBalancesSliceStatistic_balances_list_is_empty() {
        doReturn(Collections.emptyList())
                .when(walletRepository)
                .getExternalMainWalletBalances();
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder().build()))
                .when(walletRepository)
                .getInternalWalletBalances();

        PagedResult<BalancesDto> balancesSliceStatistic = walletService.getBalancesSliceStatistic(Collections.emptyList(), BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ZERO, BigDecimal.ONE, 1, 0);

        assertNotNull(balancesSliceStatistic);
        assertEquals(0, balancesSliceStatistic.getCount());
        assertTrue(balancesSliceStatistic.getItems().isEmpty());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
    }

    @Test
    public void getBalancesSliceStatistic_balances_list_is_not_empty_and_filtered_data_empty() {
        doReturn(Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.ONE)
                .totalBalance(BigDecimal.TEN)
                .totalBalanceUSD(BigDecimal.TEN)
                .totalBalanceBTC(BigDecimal.TEN)
                .build()))
                .when(walletRepository)
                .getExternalMainWalletBalances();
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .roleId(1)
                .roleName(UserRole.USER)
                .totalBalance(BigDecimal.TEN)
                .totalBalanceUSD(BigDecimal.TEN)
                .totalBalanceBTC(BigDecimal.TEN)
                .build()))
                .when(walletRepository)
                .getInternalWalletBalances();
        doReturn(Collections.singletonList(CoreCurrencyDto.builder().name("BTC").build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        PagedResult<BalancesDto> balancesSliceStatistic = walletService.getBalancesSliceStatistic(Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ONE,
                BigDecimal.ZERO, BigDecimal.ONE, 1, 0);

        assertNotNull(balancesSliceStatistic);
        assertEquals(0, balancesSliceStatistic.getCount());
        assertNotNull(balancesSliceStatistic.getItems());
        assertTrue(balancesSliceStatistic.getItems().isEmpty());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
    }

    @Test
    public void getBalancesSliceStatistic_balances_list_is_not_empty_and_filtered_data_not_empty_and_signOfMonitoring_true_and_checCoinRange_true() {
        doReturn(Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.ONE)
                .totalBalance(BigDecimal.TEN)
                .totalBalanceUSD(BigDecimal.TEN)
                .totalBalanceBTC(BigDecimal.TEN)
                .signOfCertainty(true)
                .signOfMonitoring(true)
                .coinRange(BigDecimal.ONE)
                .checkCoinRange(true)
                .usdRange(BigDecimal.ONE)
                .checkUsdRange(false)
                .build()))
                .when(walletRepository)
                .getExternalMainWalletBalances();
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .roleId(1)
                .roleName(UserRole.USER)
                .totalBalance(BigDecimal.TEN)
                .totalBalanceUSD(BigDecimal.TEN)
                .totalBalanceBTC(BigDecimal.TEN)
                .build()))
                .when(walletRepository)
                .getInternalWalletBalances();
        doReturn(Collections.singletonList(CoreCurrencyDto.builder().name("BTC").build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        PagedResult<BalancesDto> balancesSliceStatistic = walletService.getBalancesSliceStatistic(Collections.singletonList("BTC"), BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.TEN, 1, 0);

        assertNotNull(balancesSliceStatistic);
        assertEquals(1, balancesSliceStatistic.getCount());
        assertNotNull(balancesSliceStatistic.getItems());
        assertFalse(balancesSliceStatistic.getItems().isEmpty());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
    }

    @Test
    public void getBalancesSliceStatistic_balances_list_is_not_empty_and_filtered_data_not_empty_and_signOfMonitoring_true_and_checUsdRange_true() {
        doReturn(Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.ONE)
                .totalBalance(BigDecimal.TEN)
                .totalBalanceUSD(BigDecimal.TEN)
                .totalBalanceBTC(BigDecimal.TEN)
                .signOfCertainty(true)
                .signOfMonitoring(true)
                .coinRange(BigDecimal.ONE)
                .checkCoinRange(false)
                .usdRange(BigDecimal.ONE)
                .checkUsdRange(true)
                .build()))
                .when(walletRepository)
                .getExternalMainWalletBalances();
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .roleId(1)
                .roleName(UserRole.USER)
                .totalBalance(BigDecimal.TEN)
                .totalBalanceUSD(BigDecimal.TEN)
                .totalBalanceBTC(BigDecimal.TEN)
                .build()))
                .when(walletRepository)
                .getInternalWalletBalances();
        doReturn(Collections.singletonList(CoreCurrencyDto.builder().name("BTC").build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        PagedResult<BalancesDto> balancesSliceStatistic = walletService.getBalancesSliceStatistic(Collections.singletonList("BTC"), BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.TEN, 1, 0);

        assertNotNull(balancesSliceStatistic);
        assertEquals(1, balancesSliceStatistic.getCount());
        assertNotNull(balancesSliceStatistic.getItems());
        assertFalse(balancesSliceStatistic.getItems().isEmpty());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
    }

    @Test
    public void getBalancesSliceStatistic_balances_list_is_not_empty_and_filtered_data_not_empty_and_signOfMonitoring_false() {
        doReturn(Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.ONE)
                .totalBalance(BigDecimal.TEN)
                .totalBalanceUSD(BigDecimal.TEN)
                .totalBalanceBTC(BigDecimal.TEN)
                .signOfCertainty(true)
                .signOfMonitoring(false)
                .build()))
                .when(walletRepository)
                .getExternalMainWalletBalances();
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .roleId(1)
                .roleName(UserRole.USER)
                .totalBalance(BigDecimal.TEN)
                .totalBalanceUSD(BigDecimal.TEN)
                .totalBalanceBTC(BigDecimal.TEN)
                .build()))
                .when(walletRepository)
                .getInternalWalletBalances();
        doReturn(Collections.singletonList(CoreCurrencyDto.builder().name("BTC").build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        PagedResult<BalancesDto> balancesSliceStatistic = walletService.getBalancesSliceStatistic(Collections.singletonList("BTC"), BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.TEN, 1, 0);

        assertNotNull(balancesSliceStatistic);
        assertEquals(1, balancesSliceStatistic.getCount());
        assertNotNull(balancesSliceStatistic.getItems());
        assertFalse(balancesSliceStatistic.getItems().isEmpty());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
    }

    @Test
    public void getDashboardTwo_success() {
        doReturn(Collections.singletonList(ExternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.ONE)
                .totalBalance(BigDecimal.TEN)
                .totalBalanceUSD(BigDecimal.TEN)
                .totalBalanceBTC(BigDecimal.TEN)
                .signOfCertainty(true)
                .signOfMonitoring(true)
                .coinRange(BigDecimal.ONE)
                .checkCoinRange(false)
                .usdRange(BigDecimal.ONE)
                .checkUsdRange(true)
                .build()))
                .when(walletRepository)
                .getExternalMainWalletBalances();
        doReturn(Collections.singletonList(InternalWalletBalancesDto.builder()
                .currencyId(1)
                .currencyName("BTC")
                .roleId(1)
                .roleName(UserRole.USER)
                .totalBalance(BigDecimal.TEN)
                .totalBalanceUSD(BigDecimal.TEN)
                .totalBalanceBTC(BigDecimal.TEN)
                .build()))
                .when(walletRepository)
                .getInternalWalletBalances();
        doReturn(Collections.singletonList(CoreCurrencyDto.builder().name("BTC").build()))
                .when(coreCurrencyService)
                .getCachedActiveCurrencies();

        DashboardTwoDto dashboardTwo = walletService.getDashboardTwo(Collections.singletonList("BTC"), BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.TEN);

        assertNotNull(dashboardTwo);
        assertEquals(0, BigDecimal.TEN.compareTo(dashboardTwo.getExWalletBalancesUSDSum()));
        assertEquals(0, BigDecimal.TEN.compareTo(dashboardTwo.getInWalletBalancesUSDSum()));
        assertEquals(0, BigDecimal.ZERO.compareTo(dashboardTwo.getDeviationUSD()));
        assertEquals(0, dashboardTwo.getRedDeviationCount());
        assertEquals(0, dashboardTwo.getGreenDeviationCount());
        assertEquals(1, dashboardTwo.getYellowDeviationCount());
        assertEquals(1, dashboardTwo.getActiveCurrenciesCount());
        assertEquals(1, dashboardTwo.getMonitoredCurrenciesCount());

        verify(walletRepository, atLeastOnce()).getExternalMainWalletBalances();
        verify(walletRepository, atLeastOnce()).getInternalWalletBalances();
        verify(coreCurrencyService, atLeastOnce()).getCachedActiveCurrencies();
    }
}