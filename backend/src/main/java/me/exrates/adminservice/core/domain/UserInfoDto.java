package me.exrates.adminservice.core.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.core.domain.enums.UserStatus;
import me.exrates.adminservice.serializers.LocalDateTimeDeserializer;
import me.exrates.adminservice.serializers.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    private int userId;
    private String userNickname;
    private String registerIp;
    private String email;
    private String country;
    private BigDecimal balanceSumUsd;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime registrationDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime lastLoginDate;
    private String phone;
    private String verificationStatus;
    private UserRole role;
    private UserStatus status;
}