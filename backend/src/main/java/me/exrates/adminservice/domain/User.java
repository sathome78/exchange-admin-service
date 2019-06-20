package me.exrates.adminservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.exrates.adminservice.core.domain.enums.UserRole;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class User implements Serializable {

    private Long id;
    private String username;
    private String password;
    private UserRole userRole;
}
