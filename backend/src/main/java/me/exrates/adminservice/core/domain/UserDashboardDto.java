package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardDto {

    private Integer allUsersCount;
    private Integer allVerifiedUsersCount;
    private Integer allOnlineUsersCount;
    private Integer allBlockedUsersCount;
}