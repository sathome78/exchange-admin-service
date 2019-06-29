package me.exrates.adminservice.core.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.CoreCompanyWalletDto;
import me.exrates.adminservice.core.domain.CoreCurrencyPairDto;
import me.exrates.adminservice.core.domain.CoreOrderDto;
import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityDto;
import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityOptionDto;
import me.exrates.adminservice.core.domain.CoreWalletDto;
import me.exrates.adminservice.core.domain.CoreWalletOperationDto;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.ReportDto;
import me.exrates.adminservice.core.domain.UserBalancesInfoDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.UserReferralInfoDto;
import me.exrates.adminservice.core.domain.enums.ActionType;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.OrderBaseType;
import me.exrates.adminservice.core.domain.enums.TransactionSourceType;
import me.exrates.adminservice.core.domain.enums.WalletTransferStatus;
import me.exrates.adminservice.core.exceptions.AuthenticationNotAvailableException;
import me.exrates.adminservice.core.exceptions.BalanceChangeException;
import me.exrates.adminservice.core.exceptions.ForbiddenOperationException;
import me.exrates.adminservice.core.exceptions.InvalidAmountException;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.service.CoreCommissionService;
import me.exrates.adminservice.core.service.CoreCompanyWalletService;
import me.exrates.adminservice.core.service.CoreCurrencyService;
import me.exrates.adminservice.core.service.CoreOrderService;
import me.exrates.adminservice.core.service.CoreUserService;
import me.exrates.adminservice.core.service.CoreWalletService;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.domain.api.RateDto;
import me.exrates.adminservice.services.ExchangeRatesService;
import me.exrates.adminservice.utils.BigDecimalProcessingUtil;
import me.exrates.adminservice.utils.ReportOneExcelGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static me.exrates.adminservice.configurations.CacheConfiguration.USER_INFO_CACHE_BY_KEY;
import static me.exrates.adminservice.configurations.CacheConfiguration.USER_REFERRAL_INFO_CACHE_BY_ID;
import static me.exrates.adminservice.utils.CollectionUtil.isEmpty;

@Log4j2
@Service
@Transactional
public class CoreUserServiceImpl implements CoreUserService {

    private static final String USD = "USD";
    private static final String EDR = "EDR";

    private static final DateTimeFormatter FORMATTER_FOR_NAME = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm");

    private final Set<String> USER_ROLES = Stream.of(UserRole.values())
            .map(UserRole::name)
            .collect(Collectors.toSet());
    private final UserRole ROLE_DEFAULT_COMMISSION = UserRole.USER;

    private final CoreUserRepository coreUserRepository;
    private final CoreWalletService coreWalletService;
    private final CoreCurrencyService coreCurrencyService;
    private final CoreCommissionService coreCommissionService;
    private final CoreCompanyWalletService coreCompanyWalletService;
    private final CoreOrderService coreOrderService;
    private final ExchangeRatesService ratesService;
    private final Cache userInfoCache;
    private final Cache userReferralInfoCache;

    @Autowired
    public CoreUserServiceImpl(CoreUserRepository coreUserRepository,
                               CoreWalletService coreWalletService,
                               CoreCurrencyService coreCurrencyService,
                               CoreCommissionService coreCommissionService,
                               CoreCompanyWalletService coreCompanyWalletService,
                               CoreOrderService coreOrderService,
                               ExchangeRatesService ratesService,
                               @Qualifier(USER_INFO_CACHE_BY_KEY) Cache userInfoCache,
                               @Qualifier(USER_REFERRAL_INFO_CACHE_BY_ID) Cache userReferralInfoCache) {
        this.coreUserRepository = coreUserRepository;
        this.coreWalletService = coreWalletService;
        this.coreCurrencyService = coreCurrencyService;
        this.coreCommissionService = coreCommissionService;
        this.coreCompanyWalletService = coreCompanyWalletService;
        this.coreOrderService = coreOrderService;
        this.ratesService = ratesService;
        this.userInfoCache = userInfoCache;
        this.userReferralInfoCache = userReferralInfoCache;
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResult<UserInfoDto> getAllUsersInfoFromCache(FilterDto filter, Integer limit, Integer offset) {
        final String cacheKey = filter.buildCacheKey(limit, offset);

        return userInfoCache.get(cacheKey, () -> this.getAllUsersInfo(filter, limit, offset));
    }

    private PagedResult<UserInfoDto> getAllUsersInfo(FilterDto filter, Integer limit, Integer offset) {
        int newLimit = limit + 1;

        int recordsCount = coreUserRepository.getUserInfoListCount(filter, newLimit, offset);

        List<UserInfoDto> items = Collections.emptyList();
        if (recordsCount > 0) {
            items = coreUserRepository.getUserInfoList(filter, limit, offset);
        }
        PagedResult<UserInfoDto> pagedResult = new PagedResult<>();
        pagedResult.setItems(items);
        if (newLimit == recordsCount) {
            pagedResult.setHasNextPage(true);
        }
        return pagedResult;
    }

    @Transactional(readOnly = true)
    @Override
    public ReportDto getAllUsersInfoReport(FilterDto filter, Integer limit, Integer offset) throws Exception {
        final List<UserInfoDto> userInfoList = this.getAllUsersInfoFromCache(filter, limit, offset).getItems();

        if (isEmpty(userInfoList)) {
            throw new Exception("No users information found");
        }

        LocalDateTime now = LocalDateTime.now();

        return ReportDto.builder()
                .fileName(String.format("report_users_information_page_%d_date_%s", offset / limit + 1, now.format(FORMATTER_FOR_NAME)))
                .content(ReportOneExcelGeneratorUtil.generate(
                        userInfoList))
                .createdAt(now)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserDashboardDto getDashboardOne() {
        return coreUserRepository.getUsersDashboard();
    }

    @Transactional(readOnly = true)
    @Override
    public UserInfoDto getUserInfo(int userId) {
        return coreUserRepository.getUserInfo(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResult<UserBalancesInfoDto> getUserBalancesInfo(Integer userId, boolean withoutZeroBalances, List<String> currencyNames, Integer limit, Integer offset) {
        int recordsCount = coreUserRepository.getUserBalancesInfoListCount(userId, withoutZeroBalances, currencyNames);

        List<UserBalancesInfoDto> items = Collections.emptyList();
        if (recordsCount > 0) {
            items = coreUserRepository.getUserBalancesInfoList(userId, withoutZeroBalances, currencyNames, limit, offset);
        }
        PagedResult<UserBalancesInfoDto> pagedResult = new PagedResult<>();
        pagedResult.setCount(recordsCount);
        pagedResult.setItems(items);
        return pagedResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void manualBalanceChange(Integer userId, Integer currencyId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        CoreWalletDto wallet = coreWalletService.findByUserAndCurrency(userId, currencyId);

        if (amount.signum() == -1 && amount.abs().compareTo(wallet.getActiveBalance()) > 0) {
            throw new InvalidAmountException("Negative amount exceeds current balance!");
        }
        final String adminEmail = getUserEmailFromSecurityContext();

        boolean isAllowed = coreWalletService.isUserAllowedToManuallyChangeWalletBalance(coreUserRepository.getIdByEmail(adminEmail), userId);
        if (!isAllowed) {
            throw new ForbiddenOperationException(String.format("admin: %s, wallet %d", adminEmail, wallet.getId()));
        }
        changeWalletActiveBalance(amount, wallet);
    }

    private void changeWalletActiveBalance(BigDecimal amount, CoreWalletDto wallet) {
        CoreCommissionDto commission = coreCommissionService.findCommissionByTypeAndRole(OperationType.MANUAL, getUserRoleFromSecurityContext());
        BigDecimal commissionAmount = BigDecimalProcessingUtil.doAction(amount, commission.getValue(), ActionType.MULTIPLY_PERCENT);

        CoreWalletOperationDto walletOperationData = CoreWalletOperationDto.builder()
                .walletId(wallet.getId())
                .amount(amount)
                .balanceType(CoreWalletOperationDto.BalanceType.ACTIVE)
                .operationType(OperationType.MANUAL)
                .sourceId(null)
                .commission(commission)
                .commissionAmount(commissionAmount)
                .sourceType(TransactionSourceType.MANUAL)
                .build();

        WalletTransferStatus status = walletBalanceChange(walletOperationData);
        if (status != WalletTransferStatus.SUCCESS) {
            throw new BalanceChangeException(status.name());
        }
        if (commissionAmount.signum() > 0) {

            CoreCompanyWalletDto companyWallet = coreCompanyWalletService.findByCurrency(coreCurrencyService.findCachedCurrencyById(wallet.getCurrencyId()));
            coreCompanyWalletService.deposit(companyWallet, BigDecimal.ZERO, commissionAmount);
        }
    }

    private WalletTransferStatus walletBalanceChange(CoreWalletOperationDto walletOperation) {
        return coreWalletService.walletBalanceChange(walletOperation);
    }

    @Override
    public void updateUserOperationAuthority(CoreUserOperationAuthorityDto authority) {
        final Integer userId = authority.getUserId();
        final List<CoreUserOperationAuthorityOptionDto> options = authority.getOptions();

        final UserRole forUpdate = this.getUserRoleById(userId);

        if (forUpdate == UserRole.ADMINISTRATOR) {
            throw new ForbiddenOperationException("Status modification not permitted");
        }
        coreUserRepository.updateUserOperationAuthority(options, userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CoreUserOperationAuthorityOptionDto> getUserOperationTypeAuthorities(Integer userId) {
        return coreUserRepository.getUserOperationTypeAuthorities(userId);
    }

    @Override
    public void updateUserRole(UserRole newRole, Integer userId) {
        final UserRole forUpdate = this.getUserRoleById(userId);

        if (forUpdate == UserRole.ADMINISTRATOR) {
            throw new ForbiddenOperationException("Role modification not permitted");
        }
        coreUserRepository.updateUserRole(newRole, userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserRole> getAllRoles() {
        return coreUserRepository.getAllRoles();
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResult<UserReferralInfoDto> getUserReferralInfo(Integer userId, Integer limit, Integer offset) {
        Map<String, List<UserReferralInfoDto>> groupedByEmail = getUserReferralInfoFromCache(userId).stream()
                .collect(groupingBy(UserReferralInfoDto::getChildEmail));

        final Map<String, RateDto> cachedRates = ratesService.getCachedRates();

        final List<UserReferralInfoDto> userReferralInfoList = groupedByEmail.entrySet().stream()
                .map(entry -> {
                    final String childEmail = entry.getKey();
                    final List<UserReferralInfoDto> referralInfoList = entry.getValue();

                    BigDecimal summaryAmount = referralInfoList.stream()
                            .map(referralInfo -> {
                                final int childId = referralInfo.getChildId();
                                final int orderId = referralInfo.getOrderId();
                                final BigDecimal percent = referralInfo.getReferralPercent();

                                CoreOrderDto order = coreOrderService.findCachedOrderById(orderId);
                                if (isNull(order)) {
                                    return BigDecimal.ZERO;
                                }
                                CoreCurrencyPairDto currencyPair = coreCurrencyService.findCachedCurrencyPairById(order.getCurrencyPairId());
                                if (isNull(currencyPair)) {
                                    return BigDecimal.ZERO;
                                }
                                String currencyName = currencyPair.getCurrency2().getName();

                                RateDto rate = cachedRates.getOrDefault(currencyName, RateDto.zeroRate(currencyName));
                                BigDecimal usdRate = rate.getUsdRate();

                                BigDecimal commission = BigDecimal.ZERO;
                                if (childId == order.getUserId()) {
                                    commission = order.getCommissionFixedAmount();
                                    if (!currencyName.equals(USD)) {
                                        commission = BigDecimalProcessingUtil.doAction(commission, usdRate, ActionType.MULTIPLY);
                                    }
                                } else if (childId == order.getUserAcceptorId()) {
                                    OperationType operationTypeForAcceptor = order.getOperationType() == OperationType.BUY ? OperationType.SELL : OperationType.BUY;

                                    CoreCommissionDto commissionForAcceptor;
                                    if (order.getOrderBaseType() == OrderBaseType.ICO || currencyPair.getName().contains(EDR)) {
                                        commissionForAcceptor = CoreCommissionDto.zeroComission();
                                    } else {
                                        commissionForAcceptor = coreCommissionService.findCommissionByTypeAndRole(operationTypeForAcceptor, this.getUserRoleById(order.getUserAcceptorId()));
                                    }
                                    commission = BigDecimalProcessingUtil.doAction(order.getAmountConvert(), commissionForAcceptor.getValue(), ActionType.MULTIPLY_PERCENT);
                                    if (!currencyName.equals(USD)) {
                                        commission = BigDecimalProcessingUtil.doAction(commission, usdRate, ActionType.MULTIPLY);
                                    }
                                }
                                return BigDecimalProcessingUtil.doAction(commission, percent, ActionType.MULTIPLY_PERCENT);
                            })
                            .reduce(BigDecimal::add)
                            .orElse(BigDecimal.ZERO);
                    return UserReferralInfoDto.builder()
                            .childEmail(childEmail)
                            .referralLevel(referralInfoList.get(0).getReferralLevel())
                            .summaryAmount(summaryAmount)
                            .build();
                })
                .collect(toList());

        int recordsCount = userReferralInfoList.size();

        List<UserReferralInfoDto> items = Collections.emptyList();
        if (recordsCount > 0) {
            items = userReferralInfoList.stream()
                    .skip(offset)
                    .limit(limit)
                    .collect(toList());
        }
        PagedResult<UserReferralInfoDto> pagedResult = new PagedResult<>();
        pagedResult.setCount(recordsCount);
        pagedResult.setItems(items);
        return pagedResult;
    }

    private List<UserReferralInfoDto> getUserReferralInfoFromCache(Integer userId) {
        return userReferralInfoCache.get(userId, () -> coreUserRepository.getUserReferralInfoList(userId));
    }

    @Transactional(readOnly = true)
    public UserRole getUserRoleById(int id) {
        return coreUserRepository.getUserRoleById(id);
    }

    private String getUserEmailFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isNull(auth)) {
            throw new AuthenticationNotAvailableException();
        }
        return auth.getName();
    }

    private UserRole getUserRoleFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNull(authentication)) {
            throw new AuthenticationNotAvailableException();
        }
        String grantedAuthority = authentication.getAuthorities().
                stream()
                .map(GrantedAuthority::getAuthority)
                .filter(USER_ROLES::contains)
                .findFirst()
                .orElse(ROLE_DEFAULT_COMMISSION.name());
        log.debug("Granted authority: " + grantedAuthority);
        return UserRole.valueOf(grantedAuthority);
    }
}