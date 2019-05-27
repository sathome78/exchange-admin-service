package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreTransaction;

import java.util.List;

public interface CoreTransactionRepository {

    String COL_ID = "id";
    String COL_USER_ID = "user_id";
    String COL_CURRENCY_NAME = "user_id";
    String COL_AMOUNT = "amount";
    String COL_COMMISSION_AMOUNT = "commission_amount";
    String COL_SOURCE_TYPE = "source_type";
    String COL_OPERATION_TYPE = "operation_type";
    String COL_DATETIME = "datetime";
    String COL_RATE_IN_USD = "rate_in_usd";
    String COL_RATE_IN_BTC = "rate_in_btc";

    List<CoreTransaction> findAllLimited(int limit, int position);
}
