package me.exrates.adminservice.core.service;

import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityDto;
import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityOptionDto;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.ReportDto;
import me.exrates.adminservice.core.domain.UserBalancesInfoDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.domain.PagedResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface CoreUserService {

    PagedResult<UserInfoDto> getAllUsersInfoFromCache(FilterDto filter, Integer limit, Integer offset);

    ReportDto getAllUsersInfoReport(FilterDto filter, Integer limit, Integer offset) throws Exception;

    UserDashboardDto getDashboardOne();

    UserInfoDto getUserInfo(int userId);

    PagedResult<UserBalancesInfoDto> getUserBalancesInfo(Integer userId, boolean withoutZeroBalances, List<String> currencyNames, Integer limit, Integer offset);

    void manualBalanceChange(Integer userId, Integer currencyId, BigDecimal amount);

    void updateUserOperationAuthority(CoreUserOperationAuthorityDto authority);

    List<CoreUserOperationAuthorityOptionDto> getUserOperationTypeAuthorities(Integer userId);

    void updateUserRole(UserRole newRole, Integer userId);

    List<UserRole> getAllRoles();

    Set<Integer> countAllRegisteredUsers();

    Set<Integer> findAllNewUserIds();
}
