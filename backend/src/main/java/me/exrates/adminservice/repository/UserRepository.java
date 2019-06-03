package me.exrates.adminservice.repository;

import me.exrates.adminservice.domain.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findOne(String username);
}
