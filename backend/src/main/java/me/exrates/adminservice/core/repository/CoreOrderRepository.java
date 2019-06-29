package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreOrderDto;

import java.util.Map;

public interface CoreOrderRepository {

    String TABLE = "EXORDERS";
    String COL_ID = "id";
    String COL_USER_ID = "user_id";
    String COL_CP_ID = "currency_pair_id";
    String COL_OPERATION_TYPE_ID = "operation_type_id";
    String COL_EXRATE = "exrate";
    String COL_AMOUNT_BASE = "amount_base";
    String COL_AMOUNT_CONVERT = "amount_convert";
    String COL_COMMISSION_ID = "commission_id";
    String COL_COMMISSION_FIXED_AMOUNT = "commission_fixed_amount";
    String COL_USER_ACCEPTOR_ID = "user_acceptor_id";
    String COL_DATE_CREATION = "date_creation";
    String COL_DATE_ACCEPTION = "date_acception";
    String COL_STATUS_ID = "status_id";
    String COL_STATUS_MODIFICATION_DATE = "status_modification_date";
    String COL_ORDER_SOURCE_ID = "order_source_id";
    String COL_COUNTER_ORDER_ID = "counter_order_id";
    String COL_BASE_TYPE = "base_type";

    Map<String, Integer> getDailyBuySellVolume();

    int getDailyUniqueUsersQuantity();

    CoreOrderDto findOrderById(int id);
}
