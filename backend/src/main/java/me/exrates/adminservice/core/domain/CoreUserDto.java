package me.exrates.adminservice.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.domain.enums.UserRole;
import me.exrates.adminservice.domain.enums.UserStatus;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class CoreUserDto implements Serializable {

    private int id;
    private String nickname;
    private String email;
    private String phone;
    @JsonProperty("status")
    private UserStatus userStatus = UserStatus.REGISTERED;
    private String password;
    private String finpassword;
    private Date regdate;
    private String ipaddress;
    private String confirmPassword;
    private String confirmFinPassword;
    private boolean readRules;
    private UserRole role = UserRole.USER;
    private String parentEmail;
    private List<CoreUserFileDto> userFiles = Collections.emptyList();
    private String kycStatus;
    private String country;
    private String firstName;
    private String lastName;
    private Date birthDay;
    private String publicId;
}