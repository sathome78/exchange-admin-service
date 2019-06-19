package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.UserInoutStatus;

import java.util.List;
import java.util.Map;

public interface UserInoutStatusRepository {

    String TABLE = "USER_INOUT_STATUS";
    String COL_USER_ID = "user_id";
    String COL_REFILL_AMOUNT = "refill_amount_usd";
    String COL_WITHDRAW_AMOUNT = "withdraw_amount_usd";
    String COL_MODIFIED = "modified";

    Map<Integer, UserInoutStatus> findAll(List<Integer> userIds);
}
