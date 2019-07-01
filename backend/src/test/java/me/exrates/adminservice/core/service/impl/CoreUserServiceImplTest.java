package me.exrates.adminservice.core.service.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyDto;
import me.exrates.adminservice.core.domain.CoreCurrencyPairDto;
import me.exrates.adminservice.core.domain.CoreOrderDto;
import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityDto;
import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityOptionDto;
import me.exrates.adminservice.core.domain.CoreWalletDto;
import me.exrates.adminservice.core.domain.CoreWalletOperationDto;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.ReferralTransactionDto;
import me.exrates.adminservice.core.domain.ReportDto;
import me.exrates.adminservice.core.domain.UserBalancesInfoDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.UserReferralInfoDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.OrderBaseType;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.core.domain.enums.UserStatus;
import me.exrates.adminservice.core.domain.enums.WalletTransferStatus;
import me.exrates.adminservice.core.exceptions.BalanceChangeException;
import me.exrates.adminservice.core.exceptions.ForbiddenOperationException;
import me.exrates.adminservice.core.exceptions.InvalidAmountException;
import me.exrates.adminservice.core.exceptions.UserRoleNotFoundException;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.service.CoreCommissionService;
import me.exrates.adminservice.core.service.CoreCompanyWalletService;
import me.exrates.adminservice.core.service.CoreCurrencyService;
import me.exrates.adminservice.core.service.CoreOrderService;
import me.exrates.adminservice.core.service.CoreUserService;
import me.exrates.adminservice.core.service.CoreWalletService;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.services.ExchangeRatesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CoreUserServiceImplTest.InnerConfig.class)
public class CoreUserServiceImplTest {

    private static final String USER_INFO_CACHE_BY_KEY_TEST = "user-info-cache-by-key-test";
    private static final String USER_REFERRAL_CACHE_BY_ID_TEST = "user-referral-cache-by-id-test";

    @Mock
    private CoreUserRepository coreUserRepository;
    @Mock
    private CoreWalletService coreWalletService;
    @Mock
    private CoreCurrencyService coreCurrencyService;
    @Mock
    private CoreCommissionService coreCommissionService;
    @Mock
    private CoreCompanyWalletService coreCompanyWalletService;
    @Mock
    private CoreOrderService coreOrderService;
    @Mock
    private ExchangeRatesService ratesService;
    @Autowired
    @Qualifier(USER_INFO_CACHE_BY_KEY_TEST)
    private Cache userInfoCacheByKey;
    @Autowired
    @Qualifier(USER_REFERRAL_CACHE_BY_ID_TEST)
    private Cache userReferralInfoCache;

    private CoreUserService coreUserService;

    @Before
    public void setUp() throws Exception {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("admin@example.com");

        PagedResult<UserInfoDto> pagedResult = new PagedResult<>();
        pagedResult.setItems(Collections.singletonList(UserInfoDto.builder()
                .userId(1)
                .userNickname("Vasya")
                .registerIp("192.0.0.1")
                .email("vasya@example.com")
                .country("Ukraine")
                .balanceSumUsd(BigDecimal.TEN)
                .registrationDate(LocalDateTime.now().minusDays(1))
                .lastLoginDate(LocalDateTime.now())
                .phone("+31111111")
                .verificationStatus("SUCCESS")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build()));
        pagedResult.setHasNextPage(false);

        FilterDto filterDto = new FilterDto();
        final String cacheKey = filterDto.buildCacheKey(15, 0);

        userInfoCacheByKey.put(cacheKey, pagedResult);
        userReferralInfoCache.put(1, Collections.singletonList(ReferralTransactionDto.builder().build()));

        coreUserService = spy(new CoreUserServiceImpl(
                coreUserRepository,
                coreWalletService,
                coreCurrencyService,
                coreCommissionService,
                coreCompanyWalletService,
                coreOrderService,
                ratesService,
                userInfoCacheByKey,
                userReferralInfoCache));
    }

    @Test
    public void getAllUsersInfoFromCache_get_from_cache_ok() {
        PagedResult<UserInfoDto> allUsersInfoFromCache = coreUserService.getAllUsersInfoFromCache(FilterDto.builder().build(), 15, 0);

        assertNotNull(allUsersInfoFromCache);
        assertEquals(1, allUsersInfoFromCache.getItems().size());
        assertFalse(allUsersInfoFromCache.isHasNextPage());

        verify(coreUserRepository, never()).getUserInfoListCount(any(FilterDto.class), anyInt(), anyInt());
        verify(coreUserRepository, never()).getUserInfoList(any(FilterDto.class), anyInt(), anyInt());
    }

    @Test
    public void getAllUsersInfoFromCache_get_from_cache_not_present() {
        userInfoCacheByKey.clear();

        doReturn(1)
                .when(coreUserRepository)
                .getUserInfoListCount(any(FilterDto.class), anyInt(), anyInt());
        doReturn(Collections.singletonList(UserInfoDto.builder().build()))
                .when(coreUserRepository)
                .getUserInfoList(any(FilterDto.class), anyInt(), anyInt());

        PagedResult<UserInfoDto> allUsersInfoFromCache = coreUserService.getAllUsersInfoFromCache(FilterDto.builder().build(), 15, 0);

        assertNotNull(allUsersInfoFromCache);
        assertNotNull(allUsersInfoFromCache.getItems());
        assertFalse(allUsersInfoFromCache.getItems().isEmpty());
        assertEquals(1, allUsersInfoFromCache.getItems().size());
        assertFalse(allUsersInfoFromCache.isHasNextPage());

        verify(coreUserRepository, atLeastOnce()).getUserInfoListCount(any(FilterDto.class), anyInt(), anyInt());
        verify(coreUserRepository, atLeastOnce()).getUserInfoList(any(FilterDto.class), anyInt(), anyInt());
    }

    @Test
    public void getAllUsersInfoReport_ok() throws Exception {
        ReportDto allUsersInfoReport = coreUserService.getAllUsersInfoReport(FilterDto.builder().build(), 15, 0);

        assertNotNull(allUsersInfoReport);
        assertNotNull(allUsersInfoReport.getContent());
        assertNotNull(allUsersInfoReport.getFileName());
        assertNotNull(allUsersInfoReport.getCreatedAt());

        verify(coreUserRepository, never()).getUserInfoListCount(any(FilterDto.class), anyInt(), anyInt());
        verify(coreUserRepository, never()).getUserInfoList(any(FilterDto.class), anyInt(), anyInt());
    }

    @Test(expected = Exception.class)
    public void getAllUsersInfoReport_info_not_found() throws Exception {
        userInfoCacheByKey.clear();

        doReturn(0)
                .when(coreUserRepository)
                .getUserInfoListCount(any(FilterDto.class), anyInt(), anyInt());
        doReturn(Collections.emptyList())
                .when(coreUserRepository)
                .getUserInfoList(any(FilterDto.class), anyInt(), anyInt());

        coreUserService.getAllUsersInfoReport(FilterDto.builder().build(), 15, 0);

        verify(coreUserRepository, atLeastOnce()).getUserInfoListCount(any(FilterDto.class), anyInt(), anyInt());
        verify(coreUserRepository, never()).getUserInfoList(any(FilterDto.class), anyInt(), anyInt());
    }

    @Test
    public void getDashboardOne_ok() {
        doReturn(UserDashboardDto.builder().build())
                .when(coreUserRepository)
                .getUsersDashboard();

        UserDashboardDto dashboardOne = coreUserService.getDashboardOne();

        assertNotNull(dashboardOne);

        verify(coreUserRepository, atLeastOnce()).getUsersDashboard();
    }

    @Test
    public void getUserInfo_ok() {
        doReturn(UserInfoDto.builder().userId(1).build())
                .when(coreUserRepository)
                .getUserInfo(anyInt());

        UserInfoDto userInfo = coreUserService.getUserInfo(1);

        assertNotNull(userInfo);
        assertEquals(1, userInfo.getUserId());

        verify(coreUserRepository, atLeastOnce()).getUserInfo(anyInt());
    }

    @Test(expected = RuntimeException.class)
    public void getUserInfo_not_found() {
        doThrow(RuntimeException.class)
                .when(coreUserRepository)
                .getUserInfo(anyInt());

        coreUserService.getUserInfo(1);

        verify(coreUserRepository, atLeastOnce()).getUserInfo(anyInt());
    }

    @Test
    public void getUserBalancesInfo_ok() {
        doReturn(1)
                .when(coreUserRepository)
                .getUserBalancesInfoListCount(anyInt(), anyBoolean(), anyList());
        doReturn(Collections.singletonList(UserBalancesInfoDto.builder().build()))
                .when(coreUserRepository)
                .getUserBalancesInfoList(anyInt(), anyBoolean(), anyList(), anyInt(), anyInt());

        PagedResult<UserBalancesInfoDto> userBalancesInfo = coreUserService.getUserBalancesInfo(1, true, Collections.emptyList(), 15, 0);

        assertNotNull(userBalancesInfo);
        assertNotNull(userBalancesInfo.getItems());
        assertFalse(userBalancesInfo.getItems().isEmpty());
        assertEquals(1, userBalancesInfo.getItems().size());
        assertEquals(1, userBalancesInfo.getCount());

        verify(coreUserRepository, atLeastOnce()).getUserBalancesInfoListCount(anyInt(), anyBoolean(), anyList());
        verify(coreUserRepository, atLeastOnce()).getUserBalancesInfoList(anyInt(), anyBoolean(), anyList(), anyInt(), anyInt());
    }

    @Test
    public void getUserBalancesInfo_not_found() {
        doReturn(0)
                .when(coreUserRepository)
                .getUserBalancesInfoListCount(anyInt(), anyBoolean(), anyList());
        doReturn(Collections.emptyList())
                .when(coreUserRepository)
                .getUserBalancesInfoList(anyInt(), anyBoolean(), anyList(), anyInt(), anyInt());

        PagedResult<UserBalancesInfoDto> userBalancesInfo = coreUserService.getUserBalancesInfo(1, true, Collections.emptyList(), 15, 0);

        assertNotNull(userBalancesInfo);
        assertNotNull(userBalancesInfo.getItems());
        assertTrue(userBalancesInfo.getItems().isEmpty());
        assertEquals(0, userBalancesInfo.getCount());

        verify(coreUserRepository, atLeastOnce()).getUserBalancesInfoListCount(anyInt(), anyBoolean(), anyList());
        verify(coreUserRepository, never()).getUserBalancesInfoList(anyInt(), anyBoolean(), anyList(), anyInt(), anyInt());
    }

    @Test
    public void manualBalanceChange_ok() {
        doReturn(CoreWalletDto.builder().id(1).build())
                .when(coreWalletService)
                .findByUserAndCurrency(anyInt(), anyInt());
        doReturn(true)
                .when(coreWalletService)
                .isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());
        doReturn(CoreCommissionDto.builder().value(BigDecimal.ONE).build())
                .when(coreCommissionService)
                .findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        doReturn(WalletTransferStatus.SUCCESS)
                .when(coreWalletService)
                .walletBalanceChange(any(CoreWalletOperationDto.class));
        doReturn(CoreCompanyWalletDto.builder().build())
                .when(coreCompanyWalletService)
                .findByCurrency(any(CoreCurrencyDto.class));
        doReturn(CoreCurrencyDto.builder().build())
                .when(coreCurrencyService)
                .findCachedCurrencyById(anyInt());
        doNothing()
                .when(coreCompanyWalletService)
                .deposit(any(CoreCompanyWalletDto.class), any(BigDecimal.class), any(BigDecimal.class));

        coreUserService.manualBalanceChange(1, 4, BigDecimal.TEN);

        verify(coreWalletService, atLeastOnce()).findByUserAndCurrency(anyInt(), anyInt());
        verify(coreWalletService, atLeastOnce()).isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());
        verify(coreCommissionService, atLeastOnce()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        verify(coreWalletService, atLeastOnce()).walletBalanceChange(any(CoreWalletOperationDto.class));
        verify(coreCompanyWalletService, atLeastOnce()).findByCurrency(any(CoreCurrencyDto.class));
        verify(coreCurrencyService, atLeastOnce()).findCachedCurrencyById(anyInt());
        verify(coreCompanyWalletService, atLeastOnce()).deposit(any(CoreCompanyWalletDto.class), any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    public void manualBalanceChange_amount_zero() {

        coreUserService.manualBalanceChange(1, 4, BigDecimal.ZERO);

        verify(coreWalletService, never()).findByUserAndCurrency(anyInt(), anyInt());
        verify(coreWalletService, never()).isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());
        verify(coreCommissionService, never()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        verify(coreWalletService, never()).walletBalanceChange(any(CoreWalletOperationDto.class));
        verify(coreCompanyWalletService, never()).findByCurrency(any(CoreCurrencyDto.class));
        verify(coreCurrencyService, never()).findCachedCurrencyById(anyInt());
        verify(coreCompanyWalletService, never()).deposit(any(CoreCompanyWalletDto.class), any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test(expected = InvalidAmountException.class)
    public void manualBalanceChange_negative_amount() {
        doReturn(CoreWalletDto.builder().id(1).activeBalance(BigDecimal.valueOf(-100)).build())
                .when(coreWalletService)
                .findByUserAndCurrency(anyInt(), anyInt());

        coreUserService.manualBalanceChange(1, 4, BigDecimal.valueOf(-10));

        verify(coreWalletService, atLeastOnce()).findByUserAndCurrency(anyInt(), anyInt());
        verify(coreWalletService, never()).isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());
        verify(coreCommissionService, never()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        verify(coreWalletService, never()).walletBalanceChange(any(CoreWalletOperationDto.class));
        verify(coreCompanyWalletService, never()).findByCurrency(any(CoreCurrencyDto.class));
        verify(coreCurrencyService, never()).findCachedCurrencyById(anyInt());
        verify(coreCompanyWalletService, never()).deposit(any(CoreCompanyWalletDto.class), any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test(expected = ForbiddenOperationException.class)
    public void manualBalanceChange_not_allowed_change_balance() {
        doReturn(CoreWalletDto.builder().id(1).build())
                .when(coreWalletService)
                .findByUserAndCurrency(anyInt(), anyInt());
        doReturn(false)
                .when(coreWalletService)
                .isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());

        coreUserService.manualBalanceChange(1, 4, BigDecimal.TEN);

        verify(coreWalletService, atLeastOnce()).findByUserAndCurrency(anyInt(), anyInt());
        verify(coreWalletService, atLeastOnce()).isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());
        verify(coreCommissionService, never()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        verify(coreWalletService, never()).walletBalanceChange(any(CoreWalletOperationDto.class));
        verify(coreCompanyWalletService, never()).findByCurrency(any(CoreCurrencyDto.class));
        verify(coreCurrencyService, never()).findCachedCurrencyById(anyInt());
        verify(coreCompanyWalletService, never()).deposit(any(CoreCompanyWalletDto.class), any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test(expected = BalanceChangeException.class)
    public void manualBalanceChange_not_success_status() {
        doReturn(CoreWalletDto.builder().id(1).build())
                .when(coreWalletService)
                .findByUserAndCurrency(anyInt(), anyInt());
        doReturn(true)
                .when(coreWalletService)
                .isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());
        doReturn(CoreCommissionDto.builder().value(BigDecimal.ONE).build())
                .when(coreCommissionService)
                .findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        doReturn(WalletTransferStatus.WALLET_UPDATE_ERROR)
                .when(coreWalletService)
                .walletBalanceChange(any(CoreWalletOperationDto.class));

        coreUserService.manualBalanceChange(1, 4, BigDecimal.TEN);

        verify(coreWalletService, atLeastOnce()).findByUserAndCurrency(anyInt(), anyInt());
        verify(coreWalletService, atLeastOnce()).isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());
        verify(coreCommissionService, atLeastOnce()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        verify(coreWalletService, atLeastOnce()).walletBalanceChange(any(CoreWalletOperationDto.class));
        verify(coreCompanyWalletService, never()).findByCurrency(any(CoreCurrencyDto.class));
        verify(coreCurrencyService, never()).findCachedCurrencyById(anyInt());
        verify(coreCompanyWalletService, never()).deposit(any(CoreCompanyWalletDto.class), any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    public void manualBalanceChange_commission_amount_negative() {
        doReturn(CoreWalletDto.builder().id(1).activeBalance(BigDecimal.TEN).build())
                .when(coreWalletService)
                .findByUserAndCurrency(anyInt(), anyInt());
        doReturn(true)
                .when(coreWalletService)
                .isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());
        doReturn(CoreCommissionDto.builder().value(BigDecimal.ONE).build())
                .when(coreCommissionService)
                .findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        doReturn(WalletTransferStatus.SUCCESS)
                .when(coreWalletService)
                .walletBalanceChange(any(CoreWalletOperationDto.class));

        coreUserService.manualBalanceChange(1, 4, BigDecimal.valueOf(-10));

        verify(coreWalletService, atLeastOnce()).findByUserAndCurrency(anyInt(), anyInt());
        verify(coreWalletService, atLeastOnce()).isUserAllowedToManuallyChangeWalletBalance(anyInt(), anyInt());
        verify(coreCommissionService, atLeastOnce()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        verify(coreWalletService, atLeastOnce()).walletBalanceChange(any(CoreWalletOperationDto.class));
        verify(coreCompanyWalletService, never()).findByCurrency(any(CoreCurrencyDto.class));
        verify(coreCurrencyService, never()).findCachedCurrencyById(anyInt());
        verify(coreCompanyWalletService, never()).deposit(any(CoreCompanyWalletDto.class), any(BigDecimal.class), any(BigDecimal.class));
    }

    @Test
    public void updateUserOperationAuthority_ok() {
        doReturn(UserRole.USER)
                .when(coreUserRepository)
                .getUserRoleById(anyInt());
        doNothing()
                .when(coreUserRepository)
                .updateUserOperationAuthority(anyList(), anyInt());

        coreUserService.updateUserOperationAuthority(CoreUserOperationAuthorityDto.builder()
                .userId(1)
                .options(Collections.singletonList(CoreUserOperationAuthorityOptionDto.builder().build()))
                .build());

        verify(coreUserRepository, atLeastOnce()).getUserRoleById(anyInt());
        verify(coreUserRepository, atLeastOnce()).updateUserOperationAuthority(anyList(), anyInt());
    }

    @Test(expected = ForbiddenOperationException.class)
    public void updateUserOperationAuthority_user_role_administrator() {
        doReturn(UserRole.ADMINISTRATOR)
                .when(coreUserRepository)
                .getUserRoleById(anyInt());

        coreUserService.updateUserOperationAuthority(CoreUserOperationAuthorityDto.builder()
                .userId(1)
                .options(Collections.singletonList(CoreUserOperationAuthorityOptionDto.builder().build()))
                .build());

        verify(coreUserRepository, atLeastOnce()).getUserRoleById(anyInt());
        verify(coreUserRepository, never()).updateUserOperationAuthority(anyList(), anyInt());
    }

    @Test
    public void getUserOperationTypeAuthorities_ok() {
        doReturn(Collections.singletonList(CoreUserOperationAuthorityOptionDto.builder().build()))
                .when(coreUserRepository)
                .getUserOperationTypeAuthorities(anyInt());

        List<CoreUserOperationAuthorityOptionDto> authorities = coreUserService.getUserOperationTypeAuthorities(1);

        assertNotNull(authorities);
        assertFalse(authorities.isEmpty());
        assertEquals(1, authorities.size());

        verify(coreUserRepository, atLeastOnce()).getUserOperationTypeAuthorities(anyInt());
    }

    @Test
    public void getUserOperationTypeAuthorities_not_found() {
        doReturn(Collections.emptyList())
                .when(coreUserRepository)
                .getUserOperationTypeAuthorities(anyInt());

        List<CoreUserOperationAuthorityOptionDto> authorities = coreUserService.getUserOperationTypeAuthorities(1);

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());

        verify(coreUserRepository, atLeastOnce()).getUserOperationTypeAuthorities(anyInt());
    }

    @Test
    public void updateUserRole_ok() {
        doReturn(UserRole.ACCOUNTANT)
                .when(coreUserRepository)
                .getUserRoleById(anyInt());
        doNothing()
                .when(coreUserRepository)
                .updateUserRole(any(UserRole.class), anyInt());

        coreUserService.updateUserRole(UserRole.USER, 1);

        verify(coreUserRepository, atLeastOnce()).getUserRoleById(anyInt());
        verify(coreUserRepository, atLeastOnce()).updateUserRole(any(UserRole.class), anyInt());
    }

    @Test(expected = ForbiddenOperationException.class)
    public void updateUserRole_user_role_administrator() {
        doReturn(UserRole.ADMINISTRATOR)
                .when(coreUserRepository)
                .getUserRoleById(anyInt());

        coreUserService.updateUserRole(UserRole.USER, 1);

        verify(coreUserRepository, atLeastOnce()).getUserRoleById(anyInt());
        verify(coreUserRepository, never()).updateUserRole(any(UserRole.class), anyInt());
    }

    @Test
    public void getAllRoles_ok() {
        doReturn(Collections.singletonList(UserRole.USER))
                .when(coreUserRepository)
                .getAllRoles();

        List<UserRole> roles = coreUserService.getAllRoles();

        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertEquals(1, roles.size());

        verify(coreUserRepository, atLeastOnce()).getAllRoles();
    }

    @Test
    public void getUserReferralInfo_initiator_email_equal_user_id() {
        userReferralInfoCache.clear();

        doReturn(Collections.singletonList(ReferralTransactionDto.builder()
                .orderId(1)
                .initiatorId(1)
                .initiatorEmail("initiator@example.com")
                .referralLevel(1)
                .referralPercent(BigDecimal.TEN)
                .build()))
                .when(coreUserRepository)
                .getUserReferralTransactionList(anyInt());
        doReturn(Collections.singletonMap("ETH", RateDto.builder()
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.TEN)
                .build()))
                .when(ratesService)
                .getCachedRates();
        doReturn(CoreOrderDto.builder()
                .currencyPairId(1)
                .userId(1)
                .userAcceptorId(2)
                .commissionFixedAmount(BigDecimal.ONE)
                .operationType(OperationType.INPUT)
                .orderBaseType(OrderBaseType.LIMIT)
                .amountBase(BigDecimal.valueOf(100))
                .amountConvert(BigDecimal.valueOf(200))
                .build())
                .when(coreOrderService)
                .findCachedOrderById(anyInt());
        doReturn(CoreCurrencyPairDto.builder()
                .name("BTC/ETH")
                .currency2(CoreCurrencyDto.builder()
                        .name("ETH")
                        .build())
                .build())
                .when(coreCurrencyService)
                .findCachedCurrencyPairById(anyInt());

        PagedResult<UserReferralInfoDto> userReferralInfo = coreUserService.getUserReferralInfo(1, 15, 0);

        assertNotNull(userReferralInfo);
        assertNotNull(userReferralInfo.getItems());
        assertFalse(userReferralInfo.getItems().isEmpty());
        assertEquals(1, userReferralInfo.getItems().size());
        assertEquals(1, userReferralInfo.getCount());

        verify(coreUserRepository, atLeastOnce()).getUserReferralTransactionList(anyInt());
        verify(ratesService, atLeastOnce()).getCachedRates();
        verify(coreOrderService, atLeastOnce()).findCachedOrderById(anyInt());
        verify(coreCurrencyService, atLeastOnce()).findCachedCurrencyPairById(anyInt());
        verify(coreCommissionService, never()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
    }

    @Test
    public void getUserReferralInfo_initiator_email_equal_user_acceptor_id() {
        userReferralInfoCache.clear();

        doReturn(Collections.singletonList(ReferralTransactionDto.builder()
                .orderId(1)
                .initiatorId(2)
                .initiatorEmail("initiator@example.com")
                .referralLevel(1)
                .referralPercent(BigDecimal.TEN)
                .build()))
                .when(coreUserRepository)
                .getUserReferralTransactionList(anyInt());
        doReturn(Collections.singletonMap("ETH", RateDto.builder()
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.TEN)
                .build()))
                .when(ratesService)
                .getCachedRates();
        doReturn(CoreOrderDto.builder()
                .currencyPairId(1)
                .userId(1)
                .userAcceptorId(2)
                .commissionFixedAmount(BigDecimal.ONE)
                .operationType(OperationType.INPUT)
                .orderBaseType(OrderBaseType.LIMIT)
                .amountBase(BigDecimal.valueOf(100))
                .amountConvert(BigDecimal.valueOf(200))
                .build())
                .when(coreOrderService)
                .findCachedOrderById(anyInt());
        doReturn(CoreCurrencyPairDto.builder()
                .name("BTC/ETH")
                .currency2(CoreCurrencyDto.builder()
                        .name("ETH")
                        .build())
                .build())
                .when(coreCurrencyService)
                .findCachedCurrencyPairById(anyInt());
        doReturn(CoreCommissionDto.builder()
                .value(BigDecimal.valueOf(3))
                .build())
                .when(coreCommissionService)
                .findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        doReturn(UserRole.USER)
                .when(coreUserRepository)
                .getUserRoleById(anyInt());

        PagedResult<UserReferralInfoDto> userReferralInfo = coreUserService.getUserReferralInfo(1, 15, 0);

        assertNotNull(userReferralInfo);
        assertNotNull(userReferralInfo.getItems());
        assertFalse(userReferralInfo.getItems().isEmpty());
        assertEquals(1, userReferralInfo.getItems().size());
        assertEquals(1, userReferralInfo.getCount());

        verify(coreUserRepository, atLeastOnce()).getUserReferralTransactionList(anyInt());
        verify(ratesService, atLeastOnce()).getCachedRates();
        verify(coreOrderService, atLeastOnce()).findCachedOrderById(anyInt());
        verify(coreCurrencyService, atLeastOnce()).findCachedCurrencyPairById(anyInt());
        verify(coreCommissionService, atLeastOnce()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
        verify(coreUserRepository, atLeastOnce()).getUserRoleById(anyInt());
    }

    @Test
    public void getUserReferralInfo_initiator_email_equal_user_acceptor_id_and_order_base_type_ico() {
        userReferralInfoCache.clear();

        doReturn(Collections.singletonList(ReferralTransactionDto.builder()
                .orderId(1)
                .initiatorId(2)
                .initiatorEmail("initiator@example.com")
                .referralLevel(1)
                .referralPercent(BigDecimal.TEN)
                .build()))
                .when(coreUserRepository)
                .getUserReferralTransactionList(anyInt());
        doReturn(Collections.singletonMap("ETH", RateDto.builder()
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.TEN)
                .build()))
                .when(ratesService)
                .getCachedRates();
        doReturn(CoreOrderDto.builder()
                .currencyPairId(1)
                .userId(1)
                .userAcceptorId(2)
                .commissionFixedAmount(BigDecimal.ONE)
                .operationType(OperationType.INPUT)
                .orderBaseType(OrderBaseType.ICO)
                .amountBase(BigDecimal.valueOf(100))
                .amountConvert(BigDecimal.valueOf(200))
                .build())
                .when(coreOrderService)
                .findCachedOrderById(anyInt());
        doReturn(CoreCurrencyPairDto.builder()
                .name("BTC/ETH")
                .currency2(CoreCurrencyDto.builder()
                        .name("ETH")
                        .build())
                .build())
                .when(coreCurrencyService)
                .findCachedCurrencyPairById(anyInt());

        PagedResult<UserReferralInfoDto> userReferralInfo = coreUserService.getUserReferralInfo(1, 15, 0);

        assertNotNull(userReferralInfo);
        assertNotNull(userReferralInfo.getItems());
        assertFalse(userReferralInfo.getItems().isEmpty());
        assertEquals(1, userReferralInfo.getItems().size());
        assertEquals(1, userReferralInfo.getCount());

        verify(coreUserRepository, atLeastOnce()).getUserReferralTransactionList(anyInt());
        verify(ratesService, atLeastOnce()).getCachedRates();
        verify(coreOrderService, atLeastOnce()).findCachedOrderById(anyInt());
        verify(coreCurrencyService, atLeastOnce()).findCachedCurrencyPairById(anyInt());
        verify(coreCommissionService, never()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
    }

    @Test
    public void getUserReferralInfo_referral_transaction_not_found() {
        userReferralInfoCache.clear();

        doReturn(Collections.emptyList())
                .when(coreUserRepository)
                .getUserReferralTransactionList(anyInt());
        doReturn(Collections.singletonMap("ETH", RateDto.builder()
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.TEN)
                .build()))
                .when(ratesService)
                .getCachedRates();

        PagedResult<UserReferralInfoDto> userReferralInfo = coreUserService.getUserReferralInfo(1, 15, 0);

        assertNotNull(userReferralInfo);
        assertNotNull(userReferralInfo.getItems());
        assertTrue(userReferralInfo.getItems().isEmpty());
        assertEquals(0, userReferralInfo.getCount());

        verify(coreUserRepository, atLeastOnce()).getUserReferralTransactionList(anyInt());
        verify(ratesService, atLeastOnce()).getCachedRates();
        verify(coreOrderService, never()).findCachedOrderById(anyInt());
        verify(coreCurrencyService, never()).findCachedCurrencyPairById(anyInt());
        verify(coreCommissionService, never()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
    }

    @Test
    public void getUserReferralInfo_order_not_found() {
        userReferralInfoCache.clear();

        doReturn(Collections.singletonList(ReferralTransactionDto.builder()
                .orderId(1)
                .initiatorId(1)
                .initiatorEmail("initiator@example.com")
                .referralLevel(1)
                .referralPercent(BigDecimal.TEN)
                .build()))
                .when(coreUserRepository)
                .getUserReferralTransactionList(anyInt());
        doReturn(Collections.singletonMap("ETH", RateDto.builder()
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.TEN)
                .build()))
                .when(ratesService)
                .getCachedRates();
        doReturn(null)
                .when(coreOrderService)
                .findCachedOrderById(anyInt());

        PagedResult<UserReferralInfoDto> userReferralInfo = coreUserService.getUserReferralInfo(1, 15, 0);

        assertNotNull(userReferralInfo);
        assertNotNull(userReferralInfo.getItems());
        assertFalse(userReferralInfo.getItems().isEmpty());
        assertEquals(1, userReferralInfo.getItems().size());
        assertEquals(1, userReferralInfo.getCount());

        verify(coreUserRepository, atLeastOnce()).getUserReferralTransactionList(anyInt());
        verify(ratesService, atLeastOnce()).getCachedRates();
        verify(coreOrderService, atLeastOnce()).findCachedOrderById(anyInt());
        verify(coreCurrencyService, never()).findCachedCurrencyPairById(anyInt());
        verify(coreCommissionService, never()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
    }

    @Test
    public void getUserReferralInfo_currency_pair_not_found() {
        userReferralInfoCache.clear();

        doReturn(Collections.singletonList(ReferralTransactionDto.builder()
                .orderId(1)
                .initiatorId(1)
                .initiatorEmail("initiator@example.com")
                .referralLevel(1)
                .referralPercent(BigDecimal.TEN)
                .build()))
                .when(coreUserRepository)
                .getUserReferralTransactionList(anyInt());
        doReturn(Collections.singletonMap("ETH", RateDto.builder()
                .usdRate(BigDecimal.ONE)
                .btcRate(BigDecimal.TEN)
                .build()))
                .when(ratesService)
                .getCachedRates();
        doReturn(CoreOrderDto.builder()
                .currencyPairId(1)
                .userId(1)
                .userAcceptorId(2)
                .commissionFixedAmount(BigDecimal.ONE)
                .operationType(OperationType.INPUT)
                .orderBaseType(OrderBaseType.LIMIT)
                .amountBase(BigDecimal.valueOf(100))
                .amountConvert(BigDecimal.valueOf(200))
                .build())
                .when(coreOrderService)
                .findCachedOrderById(anyInt());
        doReturn(null)
                .when(coreCurrencyService)
                .findCachedCurrencyPairById(anyInt());

        PagedResult<UserReferralInfoDto> userReferralInfo = coreUserService.getUserReferralInfo(1, 15, 0);

        assertNotNull(userReferralInfo);
        assertNotNull(userReferralInfo.getItems());
        assertFalse(userReferralInfo.getItems().isEmpty());
        assertEquals(1, userReferralInfo.getItems().size());
        assertEquals(1, userReferralInfo.getCount());

        verify(coreUserRepository, atLeastOnce()).getUserReferralTransactionList(anyInt());
        verify(ratesService, atLeastOnce()).getCachedRates();
        verify(coreOrderService, atLeastOnce()).findCachedOrderById(anyInt());
        verify(coreCurrencyService, atLeastOnce()).findCachedCurrencyPairById(anyInt());
        verify(coreCommissionService, never()).findCachedCommissionByTypeAndRole(any(OperationType.class), any(UserRole.class));
    }

    @Test
    public void getUserRoleById_ok() {
        doReturn(UserRole.USER)
                .when(coreUserRepository)
                .getUserRoleById(anyInt());

        UserRole role = coreUserService.getUserRoleById(1);

        assertNotNull(role);
        assertEquals(UserRole.USER, role);
    }

    @Test(expected = UserRoleNotFoundException.class)
    public void getUserRoleById_not_found() {
        doThrow(UserRoleNotFoundException.class)
                .when(coreUserRepository)
                .getUserRoleById(anyInt());

        coreUserService.getUserRoleById(1);
    }

    @Configuration
    static class InnerConfig {

        @Bean(USER_INFO_CACHE_BY_KEY_TEST)
        public Cache cacheByName() {
            return new CaffeineCache(USER_INFO_CACHE_BY_KEY_TEST, Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build());
        }

        @Bean(USER_REFERRAL_CACHE_BY_ID_TEST)
        public Cache referralCacheById() {
            return new CaffeineCache(USER_REFERRAL_CACHE_BY_ID_TEST, Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build());
        }
    }
}