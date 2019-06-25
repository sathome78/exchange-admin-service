package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.ClosedOrder;

import java.util.List;
import java.util.Optional;

public interface ClosedOrderRepository {

    String TABLE = "CLOSED_ORDERS";
    String COL_ID = "id";
    String COL_CURRENCY_PAIR_NAME = "currency_pair_name";
    String COL_USER_ID = "user_id";
    String COL_USER_ACCEPTOR_ID = "user_acceptor_id";
    String COL_RATE = "rate";
    String COL_AMOUNT_BASE = "amount_base";
    String COL_AMOUNT_CONVERT = "amount_convert";
    String COL_AMOUNT_USD = "amount_usd";
    String COL_CLOSED = "closed";
    String COL_BASE_TYPE = "base_type";

    Optional<Integer> findMaxId();

    boolean batchInsert(List<ClosedOrder> transactions);
}
