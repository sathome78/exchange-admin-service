package me.exrates.adminservice.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CoreUser {

    private Integer userId;
    private String email;
    private String password;
    private LocalDateTime regdate;
    private String phone;
    private String user_status;
    private String user_role;
    private String use2fa;
    private String kyc_status;

}
