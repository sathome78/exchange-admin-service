package me.exrates.adminservice.core.repository.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.exceptions.CommissionsNotFoundException;
import me.exrates.adminservice.core.repository.CoreCommissionRepository;
import me.exrates.adminservice.domain.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Repository
public class CoreCommissionRepositoryImpl implements CoreCommissionRepository {

    private final NamedParameterJdbcOperations coreTemplate;

    @Autowired
    public CoreCommissionRepositoryImpl(@Qualifier("coreNPTemplate") NamedParameterJdbcOperations coreTemplate) {
        this.coreTemplate = coreTemplate;
    }

    @Override
    public CoreCommissionDto getCommission(OperationType operationType, UserRole userRole) {
        final String sql = "SELECT com.id, com.operation_type, com.date, com.value " +
                "FROM COMMISSION com " +
                "WHERE com.operation_type = :operation_type AND com.user_role = :role_id";

        final Map<String, Object> params = new HashMap<>();
        params.put("operation_type", operationType.type);
        params.put("role_id", userRole.getRole());

        try {
            return coreTemplate.queryForObject(sql, params, getCommissionRowMapper());
        } catch (Exception ex) {
            throw new CommissionsNotFoundException("Commission not found");
        }
    }

    private RowMapper<CoreCommissionDto> getCommissionRowMapper() {
        return (rs, i) -> {
            CoreCommissionDto commission = new CoreCommissionDto();
            commission.setDateOfChange(rs.getDate("date"));
            commission.setId(rs.getInt("id"));
            commission.setOperationType(OperationType.convert(rs.getInt("operation_type")));
            commission.setValue(rs.getBigDecimal("value"));
            return commission;
        };
    }
}