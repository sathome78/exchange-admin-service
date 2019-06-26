package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.UserRole;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CoreCommissionDto implements Serializable {

    private int id;
    private OperationType operationType;
    private BigDecimal value;
    private Date dateOfChange;
    private UserRole userRole;

    public CoreCommissionDto(int id) {
        this.id = id;
    }

    public static CoreCommissionDto zeroComission() {
        CoreCommissionDto commission = new CoreCommissionDto();
        commission.setId(24);
        commission.setOperationType(OperationType.OUTPUT);
        commission.setValue(BigDecimal.ZERO);
        commission.setDateOfChange(new Date());
        return commission;
    }
}
