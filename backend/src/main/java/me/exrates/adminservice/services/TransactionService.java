package me.exrates.adminservice.services;

import me.exrates.adminservice.domain.enums.RefillEventEnum;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TransactionService {

    void syncTransactions();

    Map<String, BigDecimal> getDailyCommissionRevenue();

    Map<String, BigDecimal> getDailyInnerTradeVolume();

    Map<Integer, List<Integer>> getAllUsersRefills(Collection<Integer> usersIds);

    Map<Integer, Set<RefillEventEnum>> getAllUsersRefillEvents(Collection<Integer> usersIds);
}
