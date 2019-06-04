package me.exrates.adminservice.repository;

import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.domain.User;

import java.util.List;
import java.util.Optional;

public interface AdminUserRepository {

    Optional<User> findOne(String username);

    Integer findMaxUserId();

    boolean batchUpdate(List<CoreUser> users);
}
