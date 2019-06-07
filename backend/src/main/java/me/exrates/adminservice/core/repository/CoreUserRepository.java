package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreUser;

import java.util.List;

public interface CoreUserRepository {

    String TABLE = "USERS";
    String COL_USER_ID = "user_id";
    String COL_EMAIL = "email";
    String COL_PASSWORD = "password";
    String COL_REG_DATE = "regdate";
    String COL_PHONE = "phone";
    String COL_USER_STATUS = "user_status";
    String COL_USER_ROLE = "user_role";
    String COL_IS_2FA_ENABLED = "use2fa";
    String COL_KYC_STATUS = "kyc_status";

    List<CoreUser> findAllAdmins();
}
