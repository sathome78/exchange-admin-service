package me.exrates.adminservice.services;

import java.math.BigDecimal;
import java.util.Map;

public interface TransactionService {

    void syncTransactions();

    Map<String, BigDecimal> getDailyCommissionRevenue();

    Map<String, BigDecimal> getDailyInnerTradeVolume();
}
