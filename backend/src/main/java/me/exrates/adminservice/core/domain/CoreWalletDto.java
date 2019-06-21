package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.domain.User;
import me.exrates.adminservice.utils.BigDecimalProcessingUtil;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CoreWalletDto {

    private int id;
    private int userId;
    private int currencyId;
    private BigDecimal activeBalance;
    private BigDecimal reservedBalance;
    private BigDecimal ieoReserved;
    private String currencyName;

    public BigDecimal getActiveBalance() {
        return BigDecimalProcessingUtil.normalize(activeBalance);
    }

    /**
     * Currently represents currency and balance on wallet
     * 1,2,3 -> RUB,USD,EUR respectively
     * any other value - BTC
     *
     * @return
     */
    public String getFullName() {
        final String activeBalance;
        switch (currencyId) {
            case 1:
            case 2:
            case 3:
                activeBalance = this.activeBalance.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
                break;
            default:
                activeBalance = this.activeBalance.toString();
        }
        return currencyName + " " + activeBalance;
    }
}