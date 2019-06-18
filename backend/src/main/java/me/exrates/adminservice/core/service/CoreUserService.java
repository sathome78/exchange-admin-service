package me.exrates.adminservice.core.service;

import me.exrates.adminservice.core.domain.UserOperationAuthorityOption;

import java.util.List;

public interface CoreUserService {

    void updateUserOperationAuthority(List<UserOperationAuthorityOption> options, Integer userId);
}
