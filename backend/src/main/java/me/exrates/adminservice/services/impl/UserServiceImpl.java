package me.exrates.adminservice.services.impl;

import me.exrates.adminservice.domain.User;
import me.exrates.adminservice.repository.AdminUserRepository;
import me.exrates.adminservice.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final AdminUserRepository userRepository;

    @Autowired
    public UserServiceImpl(AdminUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findOne(username).orElse(null);
    }
}
