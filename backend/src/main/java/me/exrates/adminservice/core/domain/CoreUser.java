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
    private String publicId;
    private String email;
    private String password;
    private LocalDateTime regdate;
    private String phone;
    private String userStatus;
    private String userRole;
    private boolean use2fa;
    private String kycStatus;

}
