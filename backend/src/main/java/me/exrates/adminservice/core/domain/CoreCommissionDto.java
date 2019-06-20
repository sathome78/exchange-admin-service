package me.exrates.adminservice.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.domain.enums.UserRole;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@ToString
@NoArgsConstructor
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