package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.UserRole;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.nonNull;
import static me.exrates.adminservice.utils.CollectionUtil.isNotEmpty;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FilterDto {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd_MM_yyyy");

    @Null
    private BigDecimal minBalance;
    @Null
    private BigDecimal maxBalance;

    @Null
    private LocalDate registeredFrom;
    @Null
    private LocalDate registeredTo;

    @Null
    private LocalDate lastEntryFrom;
    @Null
    private LocalDate lastEntryTo;

    private boolean isVerified;
    @Null
    private UserRole role;

    private boolean isActive;

    @Null
    private List<String> currencyNames;

    @Null
    private Integer minClosedOrders;
    @Null
    private Integer maxClosedOrders;

    @Null
    private Integer minRefillRequests;
    @Null
    private Integer maxRefillRequests;

    @Null
    private Integer minWithdrawRequests;
    @Null
    private Integer maxWithdrawRequests;

    public String buildCacheKey(Integer limit, Integer offset) {
        return String.join(
                "-",
                nonNull(minBalance) ? minBalance.toPlainString() : null,
                nonNull(maxBalance) ? maxBalance.toPlainString() : null,
                nonNull(registeredFrom) ? registeredFrom.format(FORMATTER) : null,
                nonNull(registeredTo) ? registeredTo.format(FORMATTER) : null,
                nonNull(lastEntryFrom) ? lastEntryFrom.format(FORMATTER) : null,
                nonNull(lastEntryTo) ? lastEntryTo.format(FORMATTER) : null,
                String.valueOf(isVerified),
                nonNull(role) ? role.name() : null,
                String.valueOf(isActive),
                isNotEmpty(currencyNames) ? String.join("_", currencyNames) : null,
                nonNull(minClosedOrders) ? minClosedOrders.toString() : null,
                nonNull(maxClosedOrders) ? maxClosedOrders.toString() : null,
                nonNull(minRefillRequests) ? minRefillRequests.toString() : null,
                nonNull(maxRefillRequests) ? maxRefillRequests.toString() : null,
                nonNull(minWithdrawRequests) ? minWithdrawRequests.toString() : null,
                nonNull(maxWithdrawRequests) ? maxWithdrawRequests.toString() : null,
                nonNull(limit) ? limit.toString() : null,
                nonNull(offset) ? offset.toString() : null
        );
    }
}