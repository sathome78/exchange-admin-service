package me.exrates.adminservice.core.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public interface IpLogRepository {

    String TABLE = "IP_Log";
    String COL_ID = "id";
    String COL_IP = "ip";
    String COL_USER_ID = "user_id";
    String COL_DATE = "date";
// enum('REGISTER','LOGIN_SUCCESS','WITHDRAW','CHANGE_PASSWORD','RESET_PASSWORD','TRANSFER_SEND','TRANSFER_CODE_ACCEPT','TRADE','REFILL_ADDRESS','REFILL_REQUEST')
    String COL_EVENT = "event";
    String COL_URL = "url";

    Map<Integer, LocalDateTime> findAllByUserIds(Collection<Integer> userIds);

}
