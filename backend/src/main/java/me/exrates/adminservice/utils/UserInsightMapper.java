package me.exrates.adminservice.utils;

import me.exrates.adminservice.domain.UserInoutStatus;
import me.exrates.adminservice.domain.UserInsight;
import me.exrates.adminservice.domain.api.UserInsightDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class UserInsightMapper {

    private static final BigDecimal TEN_KILOS = BigDecimal.valueOf(10000);
    private static final LocalDate TODAY = LocalDate.now();
    private static final Predicate<UserInsight> DAY = insight -> TODAY.isEqual(insight.getCreated());
    private static final Predicate<UserInsight> WEEK = insight -> insight.getCreated().isAfter(TODAY.minusDays(7));
    private static final Predicate<UserInsight> MONTH = insight -> insight.getCreated().isAfter(TODAY.minusDays(30));
    private static final Predicate<UserInsight> YEAR = insight -> insight.getCreated().isAfter(TODAY.minusDays(365));
    private static final Predicate<UserInsight> OK = insight -> true;

    private enum Field {
        REFILL_AMOUNT, WITHDRAW_AMOUNT, TRADE_COMMISSION, TRANSFER_COMMISSION, INOUT_COMMISSION, BALANCE,
        TRADE_COUNT, TRADE_AMOUNT, ONLY_REFILL, DEFAULT
    }

    public static void calculate(UserInsightDTO insightDTO, Map<Integer, UserInoutStatus> balances) {
        final UserInoutStatus status = balances.getOrDefault(insightDTO.getUserId(), UserInoutStatus.empty(insightDTO.getUserId()));
        insightDTO.setDeposit(status.getRefillAmountUsd());
        insightDTO.setWithdrawal(status.getWithdrawAmountUsd());
        insightDTO.setDepositGt10k(insightDTO.getDeposit().compareTo(TEN_KILOS) > 0);
        insightDTO.setWithdrawGt10k(insightDTO.getWithdrawal().compareTo(TEN_KILOS) > 0);
    }

    public static UserInsightDTO map(Map.Entry<Integer, Set<UserInsight>> entry) {
        return map(entry.getKey(), entry.getValue());
    }

    public static UserInsightDTO map(int userId, Set<UserInsight> entries) {
        if (entries.isEmpty()) {
            return UserInsightDTO.empty(userId);
        }
        UserInsightDTO dto = new UserInsightDTO();
        dto.setUserId(userId);

        dto.setTradeComDay(sumDecimal(entries, Field.TRADE_COMMISSION, DAY));
        dto.setTransferComDay(sumDecimal(entries, Field.TRANSFER_COMMISSION, DAY));
        dto.setInoutComDay(sumDecimal(entries, Field.INOUT_COMMISSION, DAY));
        dto.setTradeComWeek(sumDecimal(entries, Field.TRADE_COMMISSION, WEEK));
        dto.setTransferComWeek(sumDecimal(entries, Field.TRANSFER_COMMISSION, WEEK));
        dto.setInoutComWeek(sumDecimal(entries, Field.INOUT_COMMISSION, WEEK));
        dto.setTradeComMonth(sumDecimal(entries, Field.TRADE_COMMISSION, MONTH));
        dto.setTransferComMonth(sumDecimal(entries, Field.TRANSFER_COMMISSION, MONTH));
        dto.setInoutComMonth(sumDecimal(entries, Field.INOUT_COMMISSION, MONTH));
        dto.setTradeComYear(sumDecimal(entries, Field.TRADE_COMMISSION, OK));
        dto.setTransferComYear(sumDecimal(entries, Field.TRANSFER_COMMISSION, OK));
        dto.setInoutComYear(sumDecimal(entries, Field.INOUT_COMMISSION, OK));
        dto.setChangeBalanceDay(sumDecimal(entries, Field.BALANCE, DAY));
        dto.setChangeBalanceWeek(sumDecimal(entries, Field.BALANCE, WEEK));
        dto.setChangeBalanceMonth(sumDecimal(entries, Field.BALANCE, MONTH));
        dto.setChangeBalanceYear(sumDecimal(entries, Field.BALANCE, OK));
        dto.setTradeNumberDay(sumInt(entries, Field.TRADE_COUNT, DAY));
        dto.setTradeAmountDay(sumDecimal(entries, Field.TRADE_AMOUNT, DAY));
        dto.setTradeNumberWithRefillDay(manageTrades(entries, Field.REFILL_AMOUNT, DAY));
        dto.setTradeNumberWithWithdrawDay(manageTrades(entries, Field.WITHDRAW_AMOUNT, DAY));
        dto.setTradeNumberWeek(sumInt(entries, Field.TRADE_COUNT, WEEK));
        dto.setTradeAmountWeek(sumDecimal(entries, Field.TRADE_AMOUNT, WEEK));
        dto.setTradeNumberWithRefillWeek(manageTrades(entries, Field.REFILL_AMOUNT, WEEK));
        dto.setTradeNumberWithWithdrawWeek(manageTrades(entries, Field.WITHDRAW_AMOUNT, WEEK));
        dto.setTradeNumberMonth(sumInt(entries, Field.TRADE_COUNT, MONTH));
        dto.setTradeAmountMonth(sumDecimal(entries, Field.TRADE_AMOUNT, MONTH));
        dto.setTradeNumberWithRefillMonth(manageTrades(entries, Field.REFILL_AMOUNT, MONTH));
        dto.setTradeNumberWithWithdrawMonth(manageTrades(entries, Field.WITHDRAW_AMOUNT, MONTH));
        dto.setTradeNumberYear(sumInt(entries, Field.TRADE_COUNT, YEAR));
        dto.setTradeAmountYear(sumDecimal(entries, Field.TRADE_AMOUNT, OK));
        dto.setTradeNumberWithRefillYear(manageTrades(entries, Field.REFILL_AMOUNT, YEAR));
        dto.setTradeNumberWithWithdrawYear(manageTrades(entries, Field.WITHDRAW_AMOUNT, YEAR));
        dto.setNoDealsButRefilledDay(checkDeals(entries, Field.ONLY_REFILL, DAY));
        dto.setNoDealsButWithdrawAndRefilledDay(checkDeals(entries, Field.DEFAULT, DAY));
        dto.setNoDealsButRefilledWeek(checkDeals(entries, Field.ONLY_REFILL, WEEK));
        dto.setNoDealsButWithdrawAndRefilledWeek(checkDeals(entries, Field.DEFAULT, WEEK));
        dto.setNoDealsButRefilledMonth(checkDeals(entries, Field.ONLY_REFILL, MONTH));
        dto.setNoDealsButWithdrawAndRefilledMonth(checkDeals(entries, Field.DEFAULT, MONTH));
        dto.setNoDealsButRefilledYear(checkDeals(entries, Field.ONLY_REFILL, YEAR));
        dto.setNoDealsButWithdrawAndRefilledYear(checkDeals(entries, Field.DEFAULT, YEAR));
        return dto;
    }

    private static BigDecimal sumDecimal(Set<UserInsight> entries, Field field, Predicate<UserInsight> predicate) {
        switch (field) {
            case REFILL_AMOUNT:
                return entries.stream().filter(predicate).map(UserInsight::getRefillAmountUsd).reduce(BigDecimal.ZERO, BigDecimal::add);
            case WITHDRAW_AMOUNT:
                return entries.stream().filter(predicate).map(UserInsight::getWithdrawAmountUsd).reduce(BigDecimal.ZERO, BigDecimal::add);
            case TRADE_COMMISSION:
                return entries.stream().filter(predicate).map(UserInsight::getTradeCommissionUsd).reduce(BigDecimal.ZERO, BigDecimal::add);
            case TRANSFER_COMMISSION:
                return entries.stream().filter(predicate).map(UserInsight::getTransferCommissionUsd).reduce(BigDecimal.ZERO, BigDecimal::add);
            case INOUT_COMMISSION:
                return entries.stream().filter(predicate).map(UserInsight::getInoutCommissionUsd).reduce(BigDecimal.ZERO, BigDecimal::add);
            case BALANCE:
                return entries.stream().filter(predicate).map(UserInsight::getBalanceDynamicsUsd).reduce(BigDecimal.ZERO, BigDecimal::add);
            case TRADE_AMOUNT:
                return entries.stream().filter(predicate).map(UserInsight::getTradeAmountUsd).reduce(BigDecimal.ZERO, BigDecimal::add);
            default:
                throw new UnsupportedOperationException("NO DOUBLE OPS FOR FIELD: " + field.toString());
        }
    }

    private static String manageTrades(Set<UserInsight> entries, Field field, Predicate<UserInsight> predicate) {
        switch (field) {
            case REFILL_AMOUNT:
                return sumInt(entries, Field.TRADE_COUNT, predicate) + " / " + gtZero(sumDecimal(entries, Field.REFILL_AMOUNT, predicate));
            case WITHDRAW_AMOUNT:
                return sumInt(entries, Field.TRADE_COUNT, predicate) + " / " + gtZero(sumDecimal(entries, Field.WITHDRAW_AMOUNT, predicate));
            default:
                throw new UnsupportedOperationException("NO CHECK OPS FOR FIELD: " + field.toString());
        }
    }

    private static boolean checkDeals(Set<UserInsight> entries, Field field, Predicate<UserInsight> predicate) {
        boolean result = sumInt(entries, Field.TRADE_COUNT, predicate) < 1 && gtZero(sumDecimal(entries, Field.REFILL_AMOUNT, predicate));
        switch (field) {
            case ONLY_REFILL:
                return result;
            default:
                return result && gtZero(sumDecimal(entries, Field.WITHDRAW_AMOUNT, predicate));
        }
    }

    private static int sumInt(Set<UserInsight> entries, Field field, Predicate<UserInsight> predicate) {
        switch (field) {
            case TRADE_COUNT:
                return entries.stream().filter(predicate).map(i -> i.getTradeBuyCount() + i.getTradeSellCount()).reduce(0, Integer::sum);
            default:
                throw new UnsupportedOperationException("NO INTEGER OPS FOR FIELD: " + field.toString());
        }
    }

    private static boolean gtZero(BigDecimal value) {
        if (Objects.isNull(value)) {
            return false;
        }
        return value.compareTo(BigDecimal.ZERO) > 0;
    }
}
