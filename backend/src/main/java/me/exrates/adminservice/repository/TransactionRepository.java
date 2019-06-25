package me.exrates.adminservice.repository;

import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.utils.CurrencyTuple;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TransactionRepository {

    String TABLE = "TRANSACTIONS";
    String COL_ID = "id";
    String COL_USER_ID = "user_id";
    String COL_CURRENCY_NAME = "currency_name";
    String COL_ACTIVE_BALANCE_BEFORE = "active_balance_before";
    String COL_AMOUNT = "amount";
    String COL_COMMISSION_AMOUNT = "commission_amount";
    String COL_SOURCE_TYPE = "source_type";
    String COL_OPERATION_TYPE = "operation_type";
    String COL_DATETIME = "datetime";
    String COL_RATE_IN_USD = "rate_in_usd";
    String COL_RATE_IN_BTC = "rate_in_btc";
    String COL_SOURCE_ID = "source_id";
    String COL_RATE_BTC_FOR_ONE_USD = "rate_btc_for_one_usd";

    boolean batchInsert(List<CoreTransaction> transactions);

    Optional<Long> findMaxId();

    Collection<CurrencyTuple> getDailyTradeCommission();

    Map<String, BigDecimal> getDailyInnerTradeVolume();

    Map<Integer, List<Integer>> findUsersRefills(Collection<Integer> usersIds);

    Map<Integer, List<CoreTransaction>> findAllTransactions(Collection<Integer> userIds);
}
