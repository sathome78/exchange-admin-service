package me.exrates.adminservice.core.repository;

import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.UserRole;

public interface CoreCommissionRepository {

    CoreCommissionDto getCommission(OperationType operationType, UserRole userRole);
}
