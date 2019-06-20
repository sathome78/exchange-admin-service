package me.exrates.adminservice.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.TransactionSourceType;
import me.exrates.adminservice.serializers.OptionalDeserializer;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CoreCreditsOperationDto {

    private CoreUserDto user;
    private BigDecimal origAmountAtCreationRequest;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private OperationType operationType;
    private CoreCommissionDto commission;
    private CoreCurrencyDto currency;
    private CoreWalletDto wallet;
    private CoreMerchantDto merchant;
    private BigDecimal merchantCommissionAmount;
    @JsonDeserialize(using = OptionalDeserializer.class)
    private Optional<String> destination;
    @JsonDeserialize(using = OptionalDeserializer.class)
    private Optional<String> destinationTag;
    private TransactionSourceType transactionSourceType;
    private Boolean generateAdditionalRefillAddressAvailable;
    private Boolean storeSameAddressForParentAndTokens;
    private CoreUserDto recipient;
    private CoreWalletDto recipientWallet;
}