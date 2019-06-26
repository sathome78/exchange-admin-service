package me.exrates.adminservice.domain.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientManagementBoardDTO {

    @JsonProperty("registered_users_all")
    private int registeredUsersAll;

    @JsonProperty("registered_users_new")
    private int registeredUsersNew;

    @JsonProperty("not_refilled_new_users")
    private int notRefilledBalancesByNewUsers;

    @JsonProperty("registered_users_new")
    private int notRefilledBalances;


}
