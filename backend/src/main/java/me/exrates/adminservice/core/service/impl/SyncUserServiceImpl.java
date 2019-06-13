package me.exrates.adminservice.core.service.impl;

import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.service.SyncUserService;
import me.exrates.adminservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncUserServiceImpl implements SyncUserService {

    private final String DECRYPT_PREFIX = "{bcrypt}";

    @Value("${sync.properties.users-chunk-size:100}")
    private int limit;

    private final UserRepository adminUserRepository;
    private final CoreUserRepository coreUserRepository;

    @Autowired
    public SyncUserServiceImpl(UserRepository adminUserRepository,
                               CoreUserRepository coreUserRepository) {
        this.adminUserRepository = adminUserRepository;
        this.coreUserRepository = coreUserRepository;
    }

    @Override
    public void syncUsers() {
        final List<CoreUser> users = coreUserRepository.findAllAdmins();
        users.forEach(coreUser -> coreUser.setPassword(DECRYPT_PREFIX + coreUser.getPassword()));
        adminUserRepository.batchUpdate(users);
    }
}
