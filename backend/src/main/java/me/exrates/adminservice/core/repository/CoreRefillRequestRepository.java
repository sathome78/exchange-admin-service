package me.exrates.adminservice.core.repository;

import java.util.Collection;
import java.util.Map;

public interface CoreRefillRequestRepository {

    String TABLE = "REFILL_REQUEST";
    String COL_ID = "id";
    String COL_AMOUNT = "amount";
    String COL_DATE_CREATION = "date_creation";
    String COL_STATUS_ID = "status_id";
    String COL_STATUS_MODIFICATION_DATE = "status_modification_date";
    String COL_CURRENCY_ID = "currency_id";
    String COL_USER_ID = "user_id";
    String COL_COMMISSION_ID = "commission_id";
    String COL_MERCHANT_ID = "merchant_id";
    String COL_MERCHANT_TRANSACTION_ID = "merchant_transaction_id";
    String COL_REFILL_REQUEST_PARAM_ID = "refill_request_param_id";
    String COL_REFILL_REQUEST_ADDRESS_ID = "refill_request_address_id";
    String COL_ADMIN_HOLDER_ID = "admin_holder_id";
    String COL_IMPORT_NOTE = "import_note";
    String COL_REMARK = "remark";
    String COL_INNER_TRANSFER_HASH = "inner_transfer_hash";

    Map<Integer, Integer> getRefillAddressGeneratedByUserIds(Collection<Integer> userIds);
}
