package me.exrates.adminservice.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TransactionService {

    void syncTransactions();

    Map<String, BigDecimal> getDailyCommissionRevenue();

    Map<String, BigDecimal> getDailyInnerTradeVolume();

    Map<Integer, List<Integer>> getAllUsersRefills(Collection<Integer> usersIds);
}
