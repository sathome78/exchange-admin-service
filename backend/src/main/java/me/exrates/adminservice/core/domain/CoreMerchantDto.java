package me.exrates.adminservice.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.MerchantProcessType;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class CoreMerchantDto implements Serializable {

    private int id;
    private String name;
    private String description;
    private String serviceBeanName;
    private MerchantProcessType processType;
    private Integer refillOperationCountLimitForUserPerDay;
    private Boolean additionalTagForWithdrawAddressIsUsed;
    private Integer tokensParrentId;
    private Boolean needVerification;


    public CoreMerchantDto(int id) {
        this.id = id;
    }

    public CoreMerchantDto(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}