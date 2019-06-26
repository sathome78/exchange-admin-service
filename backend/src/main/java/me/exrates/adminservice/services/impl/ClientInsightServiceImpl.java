package me.exrates.adminservice.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.exceptions.CommonAPIException;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.repository.IpLogRepository;
import me.exrates.adminservice.core.service.CoreRefillRequestService;
import me.exrates.adminservice.domain.ClosedOrder;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.ClientInsightDTO;
import me.exrates.adminservice.domain.enums.ApiErrorsEnum;
import me.exrates.adminservice.domain.enums.OperationPeriodEnum;
import me.exrates.adminservice.domain.enums.RefillEventEnum;
import me.exrates.adminservice.repository.UserInsightRepository;
import me.exrates.adminservice.services.ClientInsightService;
import me.exrates.adminservice.services.OrderService;
import me.exrates.adminservice.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Log4j2
public class ClientInsightServiceImpl implements ClientInsightService {

    private final CoreRefillRequestService coreRefillRequestService;
    private final CoreUserRepository coreUserRepository;
    private final IpLogRepository ipLogRepository;
    private final OrderService orderService;
    private final TransactionService transactionService;
    private final UserInsightRepository userInsightRepository;

    @Autowired
    public ClientInsightServiceImpl(UserInsightRepository userInsightRepository,
                                    CoreUserRepository coreUserRepository,
                                    CoreRefillRequestService coreRefillRequestService,
                                    IpLogRepository ipLogRepository,
                                    OrderService orderService,
                                    TransactionService transactionService) {
        this.userInsightRepository = userInsightRepository;
        this.coreUserRepository = coreUserRepository;
        this.coreRefillRequestService = coreRefillRequestService;
        this.ipLogRepository = ipLogRepository;
        this.orderService = orderService;
        this.transactionService = transactionService;
    }

    @Override
    public PagedResult<ClientInsightDTO> findAll(int limit, int offset) {
        final Set<Integer> userIds = userInsightRepository.getActiveUserIds(limit, offset);
        boolean hasNextPage = limit < 1 ? userIds.size() > 20 : userIds.size() > limit;
        return getClientsInsights(hasNextPage, userIds);
    }

    @Override
    public PagedResult<ClientInsightDTO> findAll(String username) {
        String message = "Failed to find core user for username: " + username;
        final CoreUser coreUser = coreUserRepository.findByUsername(username)
                .orElseThrow(() -> new CommonAPIException(ApiErrorsEnum.USER_NOT_FOUND, message));

        return getClientsInsights(false, ImmutableList.of(coreUser.getUserId()));
    }

    @Override
    public PagedResult<ClientInsightDTO> findAll(int userId) {
        String message = "Failed to find core user for id: " + userId;
        final CoreUser coreUser = coreUserRepository.findById(userId)
                .orElseThrow(() -> new CommonAPIException(ApiErrorsEnum.USER_NOT_FOUND, message));

        return getClientsInsights(false, ImmutableList.of(coreUser.getUserId()));
    }

    private PagedResult<ClientInsightDTO> getClientsInsights(boolean hasNextPage, Collection<Integer> userIds) {
        final Map<Integer, String> allUsersIdAndEmail = coreUserRepository.findAllUsersIdAndEmail();
        final Map<Integer, LocalDateTime> usersIps = ipLogRepository.findAllByUserIds(userIds);
        final Map<Integer, Map<OperationPeriodEnum, Integer>> coinsCounter = coreRefillRequestService.findAllAddressesByUserIds(userIds);
        final Map<Integer, List<Integer>> usersRefills = transactionService.getAllUsersRefills(userIds);
        final Map<Integer, List<CoreTransaction>> transactions = transactionService.findAllTransactions(userIds);
        final Map<Integer, Set<RefillEventEnum>> refillEvents = transactionService.getAllUsersRefillEvents(transactions, userIds);
        final Map<Integer, List<ClosedOrder>> closedOrders = orderService.findAllUserClosedOrders(userIds);

        List<ClientInsightDTO> insightDTOS = Lists.newArrayList();
        userIds.forEach(userId -> {
            ClientInsightDTO dto = new ClientInsightDTO();
            dto.setUserId(userId);
            dto.setEmail(allUsersIdAndEmail.getOrDefault(userId, ""));
            dto.setLastLogin(usersIps.get(userId));
            dto.setNotRefilledCoins48Hours(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, OperationPeriodEnum.LAST_2_DAYS));
            dto.setNotRefilledCoins7Days(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, OperationPeriodEnum.LAST_7_DAYS));
            dto.setNotRefilledCoins30Days(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, OperationPeriodEnum.LAST_30_DAYS));
            dto.setNotRefilledCoins90Days(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, OperationPeriodEnum.LAST_90_DAYS));
            final int refillsSize = usersRefills.getOrDefault(userId, Collections.emptyList()).size();
            dto.setFirstRefill(refillsSize == 1);
            dto.setTwinRefill(refillsSize == 2);

            final List<ClosedOrder> userOrders = closedOrders.get(userId);
            final List<CoreTransaction> userTransactions = transactions.get(userId);

            dto.setRefillAndTrade(refillsSize > 0 && userOrders.size() == 1);
            dto.setZeroedBalance(refillEvents.get(userId).contains(RefillEventEnum.ZEROED));
            dto.setReanimateBalance(refillEvents.get(userId).contains(RefillEventEnum.REANIMATED));
            dto.setFirstRefillNoOps48Hours(dto.isFirstRefill() && checkOps(userTransactions, OperationPeriodEnum.LAST_2_DAYS));
            dto.setFirstRefillNoOps7Days(dto.isFirstRefill() && checkOps(userTransactions, OperationPeriodEnum.LAST_7_DAYS));
            dto.setFirstRefillNoOps30Days(dto.isFirstRefill() && checkOps(userTransactions, OperationPeriodEnum.LAST_30_DAYS));
            dto.setFirstRefillNoOps90Days(dto.isFirstRefill() && checkOps(userTransactions, OperationPeriodEnum.LAST_90_DAYS));
            dto.setHasTradesIn24Hours(checkTrades(userOrders, OperationPeriodEnum.LAST_DAY));
            dto.setHasTradesIn7Days(checkTrades(userOrders, OperationPeriodEnum.LAST_7_DAYS));
            dto.setHasTradesIn30Days(checkTrades(userOrders, OperationPeriodEnum.LAST_30_DAYS));
            dto.setHasTradesIn90Days(checkTrades(userOrders, OperationPeriodEnum.LAST_90_DAYS));
            dto.setHasTradesIn365Days(checkTrades(userOrders, OperationPeriodEnum.LAST_365_DAYS));
            dto.setHasRefillBasic(checkRefills(userTransactions, BigDecimal.ZERO, BigDecimal.valueOf(100)));
            dto.setHasRefillProfi(checkRefills(userTransactions, BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));
            dto.setHasRefillGold(checkRefills(userTransactions, BigDecimal.valueOf(1000), BigDecimal.valueOf(50000)));
            dto.setHasRefillExecutive(checkRefills(userTransactions, BigDecimal.valueOf(50000), BigDecimal.valueOf(100000)));
            dto.setHasTradesBasic(checkTrades(userOrders, BigDecimal.ZERO, BigDecimal.valueOf(1000)));
            dto.setHasTradesProfi(checkTrades(userOrders, BigDecimal.valueOf(1000), BigDecimal.valueOf(50000)));
            dto.setHasTradesGold(checkTrades(userOrders, BigDecimal.valueOf(50000), BigDecimal.valueOf(500000)));
            dto.setHasTradesExecutive(checkTrades(userOrders, BigDecimal.valueOf(500000), BigDecimal.valueOf(10000000)));
            dto.setHasNoTradesIn24Hours(checkNoTrades(userOrders, OperationPeriodEnum.LAST_DAY));
            dto.setHasNoTradesIn7Days(checkNoTrades(userOrders, OperationPeriodEnum.LAST_7_DAYS));
            dto.setHasNoTradesIn30Days(checkNoTrades(userOrders, OperationPeriodEnum.LAST_30_DAYS));
            dto.setHasNoTradesIn90Days(checkNoTrades(userOrders, OperationPeriodEnum.LAST_90_DAYS));
            dto.setHasNoTradesIn24HoursWithdraw(checkNoTradesButWithdraw(userOrders, userTransactions, OperationPeriodEnum.LAST_DAY));
            dto.setHasNoTradesIn7DaysWithdraw(checkNoTradesButWithdraw(userOrders, userTransactions, OperationPeriodEnum.LAST_7_DAYS));
            dto.setHasNoTradesIn30DaysWithdraw(checkNoTradesButWithdraw(userOrders, userTransactions, OperationPeriodEnum.LAST_30_DAYS));
            dto.setHasNoTradesIn90DaysWithdraw(checkNoTradesButWithdraw(userOrders, userTransactions, OperationPeriodEnum.LAST_90_DAYS));
            dto.setHasTradesReanimatedIn24Hours(dto.isReanimateBalance() && checkTrades(userOrders, OperationPeriodEnum.LAST_DAY));
            dto.setHasTradesReanimatedIn30Days(dto.isReanimateBalance() && checkTrades(userOrders, OperationPeriodEnum.LAST_30_DAYS));
            dto.setHasTradesReanimatedIn90Days(dto.isReanimateBalance() && checkTrades(userOrders, OperationPeriodEnum.LAST_90_DAYS));
            dto.setHasTradesReanimatedIn1Year(dto.isReanimateBalance() && checkTrades(userOrders, OperationPeriodEnum.LAST_365_DAYS));
            dto.setHasTradesReanimatedIn3Years(dto.isReanimateBalance() && checkTrades(userOrders, OperationPeriodEnum.LAST_3_YEARS));
            dto.setHasTradesReanimatedIn5Years(dto.isReanimateBalance() && checkTrades(userOrders, OperationPeriodEnum.LAST_5_YEARS));
            insightDTOS.add(dto);
        });
        return new PagedResult<>(hasNextPage, insightDTOS);
    }

    private boolean checkRefills(List<CoreTransaction> transactions, BigDecimal from, BigDecimal to) {
        final LocalDateTime bound = OperationPeriodEnum.getBound(OperationPeriodEnum.LAST_30_DAYS);
        final BigDecimal value = transactions.stream()
                .filter(coreTransaction -> (coreTransaction.getDateTime().isEqual(bound) || coreTransaction.getDateTime().isAfter(bound))
                        && coreTransaction.getSourceType().equalsIgnoreCase("REFILL"))
                .map(CoreTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return value.compareTo(from) > 0 && value.compareTo(to) < 0;
    }

    private boolean checkOps(List<CoreTransaction> transactions, OperationPeriodEnum type) {
        final LocalDateTime bound = OperationPeriodEnum.getBound(type);
        return transactions.stream()
                .filter(coreTransaction -> coreTransaction.getDateTime().isEqual(bound) || coreTransaction.getDateTime().isAfter(bound))
                .count() < 2;
    }

    private boolean checkTrades(List<ClosedOrder> orders, OperationPeriodEnum type) {
        final LocalDateTime bound = OperationPeriodEnum.getBound(type);
        return orders.stream()
                .filter(order -> order.getClosedDate().isEqual(bound.toLocalDate()) || order.getClosedDate().isAfter(bound.toLocalDate()))
                .count() > 1;
    }

    private boolean checkNoTrades(List<ClosedOrder> orders, OperationPeriodEnum type) {
        final LocalDateTime bound = OperationPeriodEnum.getBound(type);
        return orders.stream()
                .noneMatch(order -> order.getClosedDate().isEqual(bound.toLocalDate()) || order.getClosedDate().isAfter(bound.toLocalDate()));
    }

    private boolean checkNoTradesButWithdraw(List<ClosedOrder> orders, List<CoreTransaction> transactions, OperationPeriodEnum type) {
        final LocalDateTime bound = OperationPeriodEnum.getBound(type);
        boolean hasNoTrades = orders.stream()
                .noneMatch(order -> order.getClosedDate().isEqual(bound.toLocalDate()) || order.getClosedDate().isAfter(bound.toLocalDate()));
        boolean hasWithdraw = transactions.stream()
                .anyMatch(coreTransaction -> coreTransaction.getDateTime().isEqual(bound) || coreTransaction.getDateTime().isAfter(bound)
                        && coreTransaction.getSourceType().equalsIgnoreCase("WITHDRAW"));
        return hasNoTrades && hasWithdraw;
    }

    private boolean checkTrades(List<ClosedOrder> orders, BigDecimal from, BigDecimal to) {
        final LocalDateTime bound = OperationPeriodEnum.getBound(OperationPeriodEnum.LAST_30_DAYS);
        BigDecimal value = orders.stream()
                .filter(order -> order.getClosedDate().isEqual(bound.toLocalDate()) || order.getClosedDate().isAfter(bound.toLocalDate()))
                .map(ClosedOrder::getAmountUsd)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return value.compareTo(from) > 0 && value.compareTo(to) < 0;
    }

    @Override
    public void reloadCache(Set userIds) {

    }

}
