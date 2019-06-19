package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.UserOperationAuthorityOption;
import me.exrates.adminservice.domain.enums.UserRole;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    UserRole getUserRoleById(Integer userId);

    void updateUserOperationAuthority(List<UserOperationAuthorityOption> options, Integer userId);

    UserDashboardDto getUsersDashboard();

    int getUserInfoListCount(FilterDto filter, Integer limit, Integer offset);

    List<UserInfoDto> getUserInfoList(FilterDto filter, int limit, int offset);

    UserInfoDto getUserInfo(int userId);
}