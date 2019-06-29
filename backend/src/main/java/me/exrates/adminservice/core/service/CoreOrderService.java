package me.exrates.adminservice.core.service;

import me.exrates.adminservice.core.domain.CoreOrderDto;
import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityDto;
import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityOptionDto;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.ReportDto;
import me.exrates.adminservice.core.domain.UserBalancesInfoDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.UserReferralInfoDto;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.domain.PagedResult;

import java.math.BigDecimal;
import java.util.List;

public interface CoreOrderService {

    CoreOrderDto findCachedOrderById(int id);
}