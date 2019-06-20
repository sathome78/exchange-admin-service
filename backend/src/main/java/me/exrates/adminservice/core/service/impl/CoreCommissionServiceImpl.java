package me.exrates.adminservice.core.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.repository.CoreCommissionRepository;
import me.exrates.adminservice.core.service.CoreCommissionService;
import me.exrates.adminservice.domain.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
public class CoreCommissionServiceImpl implements CoreCommissionService {

    private final CoreCommissionRepository coreCommissionRepository;

    @Autowired
    public CoreCommissionServiceImpl(CoreCommissionRepository coreCommissionRepository) {
        this.coreCommissionRepository = coreCommissionRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public CoreCommissionDto findCommissionByTypeAndRole(OperationType operationType, UserRole userRole) {
        return coreCommissionRepository.getCommission(operationType, userRole);
    }
}