package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.UserOperationAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOperationAuthorityOption {

    private UserOperationAuthority userOperationAuthority;
    private Boolean enabled;
}