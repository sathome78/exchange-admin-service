package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreUser;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CoreUserRepository {

    String TABLE = "USER";
    String COL_USER_ID = "id";
    String COL_PUBLIC_ID = "pub_id";
    String COL_EMAIL = "email";
    String COL_PASSWORD = "password";
    String COL_REG_DATE = "regdate";
    String COL_PHONE = "phone";
    String COL_USER_STATUS = "status";
    String COL_USER_ROLE = "user_role";
    String COL_IS_2FA_ENABLED = "use2fa";
    String COL_KYC_STATUS = "kyc_status";

    List<CoreUser> findAllAdmins();

    Optional<CoreUser> findById(int userId);

    Optional<CoreUser> findByUsername(String username);

    Map<Integer, String> findAllUsersIdAndEmail();
}
