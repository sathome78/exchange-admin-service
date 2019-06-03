package me.exrates.adminservice.services;

import me.exrates.adminservice.domain.User;

public interface UserService {
    User findByUsername(String username);
}
