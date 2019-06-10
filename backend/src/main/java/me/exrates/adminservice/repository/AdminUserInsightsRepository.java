package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.UserInsight;

public interface AdminUserInsightsRepository {

    String TABLE = "USER_ANNUAL_INSIGHTS";
    String COL_CREATED = "created";
    String COL_USER_ID = "user_id";
    String COL_RATE_BTC_FOR_ONE_USD = "rate_btc_for_one_usd";
    String COL_REFILL_AMOUNT_USD = "refill_amount_usd";
    String COL_WITHDRAW_AMOUNT_USD = "withdraw_amount_usd";
    String COL_INOUT_COMMISSION_USD = "inout_commission_usd";
    String COL_TRANSFER_AMOUNT_USD = "transfer_amount_usd";
    String COL_TRANSFER_COMMISSION_USD = "transfer_commission_usd";
    String COL_TRADE_AMOUNT_USD = "trade_amount_usd";
    String COL_TRADE_COMMISSION_USD = "trade_commission_usd";
    String COL_BALANCE_DYNAMICS_USD = "balance_dynamics_usd";
    String COL_SOURCE_IDS = "source_ids";

    PagedResult<UserInsight> findAll(int limit, int offset);
}
