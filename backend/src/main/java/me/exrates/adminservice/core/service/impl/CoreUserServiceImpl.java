package me.exrates.adminservice.core.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.ReportDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.UserOperationAuthorityOption;
import me.exrates.adminservice.core.exceptions.ForbiddenOperationException;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.service.CoreUserService;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.enums.UserRole;
import me.exrates.adminservice.utils.ReportOneExcelGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static me.exrates.adminservice.configurations.CacheConfiguration.USER_INFO_CACHE_BY_KEY;
import static me.exrates.adminservice.utils.CollectionUtil.isEmpty;

@Log4j2
@Service
@Transactional
public class CoreUserServiceImpl implements CoreUserService {

    private static final DateTimeFormatter FORMATTER_FOR_NAME = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm");

    private final CoreUserRepository coreUserRepository;
    private final Cache userInfoCache;

    @Autowired
    public CoreUserServiceImpl(CoreUserRepository coreUserRepository,
                               @Qualifier(USER_INFO_CACHE_BY_KEY) Cache userInfoCache) {
        this.coreUserRepository = coreUserRepository;
        this.userInfoCache = userInfoCache;
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResult<UserInfoDto> getAllUsersInfoFromCache(FilterDto filter, Integer limit, Integer offset) {
        final String cacheKey = filter.buildCacheKey(limit, offset);

        return userInfoCache.get(cacheKey, () -> this.getAllUsersInfo(filter, limit, offset));
    }

    private PagedResult<UserInfoDto> getAllUsersInfo(FilterDto filter, Integer limit, Integer offset) {
        int newLimit = limit + 1;

        int recordsCount = coreUserRepository.getUserInfoListCount(filter, newLimit, offset);

        List<UserInfoDto> items = Collections.emptyList();
        if (recordsCount > 0) {
            items = coreUserRepository.getUserInfoList(filter, limit, offset);
        }
        PagedResult<UserInfoDto> pagedResult = new PagedResult<>();
        pagedResult.setItems(items);
        if (newLimit == recordsCount) {
            pagedResult.setHasNextPage(true);
        }
        return pagedResult;
    }

    @Transactional(readOnly = true)
    @Override
    public ReportDto getAllUsersInfoReport(FilterDto filter, Integer limit, Integer offset) throws Exception {
        final List<UserInfoDto> userInfoList = this.getAllUsersInfoFromCache(filter, limit, offset).getItems();

        if (isEmpty(userInfoList)) {
            throw new Exception("No users information found");
        }

        return ReportDto.builder()
                .fileName(String.format("report_users_information_page_%d_date_%s", offset / limit + 1, LocalDateTime.now().format(FORMATTER_FOR_NAME)))
                .content(ReportOneExcelGeneratorUtil.generate(
                        userInfoList))
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public UserDashboardDto getDashboardOne() {
        return coreUserRepository.getUsersDashboard();
    }

    @Override
    public void updateUserOperationAuthority(List<UserOperationAuthorityOption> options, Integer userId) {
        final UserRole forUpdate = coreUserRepository.getUserRoleById(userId);

        if (forUpdate == UserRole.ADMINISTRATOR) {
            throw new ForbiddenOperationException("Status modification not permitted");
        }
        coreUserRepository.updateUserOperationAuthority(options, userId);
    }
}