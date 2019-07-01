package me.exrates.adminservice.core.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.core.repository.CoreCommissionRepository;
import me.exrates.adminservice.core.service.CoreCommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static me.exrates.adminservice.configurations.CacheConfiguration.COMMISSION_CACHE_BY_ROLE_AND_TYPE;

@Log4j2
@Service
@Transactional
public class CoreCommissionServiceImpl implements CoreCommissionService {

    private final CoreCommissionRepository coreCommissionRepository;
    private final Cache commissionCacheByRoleAndType;

    @Autowired
    public CoreCommissionServiceImpl(CoreCommissionRepository coreCommissionRepository,
                                     @Qualifier(COMMISSION_CACHE_BY_ROLE_AND_TYPE) Cache commissionCacheByRoleAndType) {
        this.coreCommissionRepository = coreCommissionRepository;
        this.commissionCacheByRoleAndType = commissionCacheByRoleAndType;
    }

    @Transactional(readOnly = true)
    @Override
    public CoreCommissionDto findCachedCommissionByTypeAndRole(OperationType operationType, UserRole userRole) {
        final String cacheKey = String.join("-", userRole.name(), operationType.name());

        return commissionCacheByRoleAndType.get(cacheKey, () -> coreCommissionRepository.getCommission(operationType, userRole));
    }
}