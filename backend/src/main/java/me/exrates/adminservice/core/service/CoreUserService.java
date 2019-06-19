package me.exrates.adminservice.core.service;

import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.ReportDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.UserOperationAuthorityOption;
import me.exrates.adminservice.domain.PagedResult;

import java.util.List;

public interface CoreUserService {

    PagedResult<UserInfoDto> getAllUsersInfoFromCache(FilterDto filter, Integer limit, Integer offset);

    ReportDto getAllUsersInfoReport(FilterDto filter, Integer limit, Integer offset) throws Exception;

    UserDashboardDto getDashboardOne();

    void updateUserOperationAuthority(List<UserOperationAuthorityOption> options, Integer userId);
}