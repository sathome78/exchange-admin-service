package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.UserBalancesInfoDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityOptionDto;
import me.exrates.adminservice.core.domain.enums.UserRole;

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

    Integer getIdByEmail(String email);

    Map<Integer, String> findAllUsersIdAndEmail();

    UserRole getUserRoleById(Integer userId);

    UserDashboardDto getUsersDashboard();

    Integer getUserInfoListCount(FilterDto filter, Integer limit, Integer offset);

    List<UserInfoDto> getUserInfoList(FilterDto filter, int limit, int offset);

    UserInfoDto getUserInfo(int userId);

    Integer getUserBalancesInfoListCount(int userId, boolean withoutZeroBalances, List<String> currencyNames);

    List<UserBalancesInfoDto> getUserBalancesInfoList(int userId, boolean withoutZeroBalances, List<String> currencyNames, int limit, int offset);

    void updateUserOperationAuthority(List<CoreUserOperationAuthorityOptionDto> options, Integer userId);

    List<CoreUserOperationAuthorityOptionDto> getUserOperationTypeAuthorities(Integer userId);

    List<UserRole> getAllRoles();

    void updateUserRole(UserRole newRole, Integer userId);
}