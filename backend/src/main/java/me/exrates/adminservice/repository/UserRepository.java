package me.exrates.adminservice.repository;

import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    String TABLE = "USERS";
    String COL_USER_ID = "user_id";
    String COL_PUB_ID = "pub_id";
    String COL_EMAIL = "email";
    String COL_PASSWORD = "password";
    String COL_REGDATE = "regdate";
    String COL_PHONE = "phone";
    String COL_USER_STATUS = "user_status";
    String COL_USER_ROLE = "user_role";
    String COL_USE2FA = "use2fa";
    String COL_KYC_STATUS = "kyc_status";

    Optional<User> findOne(String username);

    boolean batchUpdate(List<CoreUser> users);
}
