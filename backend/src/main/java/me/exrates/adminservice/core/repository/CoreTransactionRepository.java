package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreTransaction;
import me.exrates.adminservice.core.domain.CoreTransactionDto;

import java.util.List;

public interface CoreTransactionRepository {

    String TABLE_NAME = "TRANSACTION";
    String COL_ID = "id";
    String COL_USER_ID = "user_id";
    String COL_CURRENCY_NAME = "currency_name";
    String COL_AMOUNT = "amount";
    String COL_COMMISSION_AMOUNT = "commission_amount";
    String COL_SOURCE_TYPE = "source_type";
    String COL_OPERATION_TYPE = "operation_type";
    String COL_DATETIME = "datetime";
    String COL_RATE_IN_USD = "rate_in_usd";
    String COL_RATE_IN_BTC = "rate_in_btc";
    String COL_SOURCE_ID = "source_id";
    String COL_ACTIVE_BALANCE_BEFORE = "active_balance_before";
    String UPDATE_CURSOR_SQL = "REPLACE INTO CURSORS (last_id, table_name, table_column) SELECT MAX(id), 'TRANSACTION', 'id' FROM TRANSACTIONS;";

    List<CoreTransaction> findAllLimited(int limit, long position);

    CoreTransactionDto create(CoreTransactionDto transaction);

    boolean updateForProvided(CoreTransactionDto transaction);
}
