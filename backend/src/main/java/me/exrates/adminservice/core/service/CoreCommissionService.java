package me.exrates.adminservice.core.service;

import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.domain.enums.UserRole;

public interface CoreCommissionService {

    CoreCommissionDto findCommissionByTypeAndRole(OperationType operationType, UserRole userRole);
}
