package me.exrates.adminservice.core.service.impl;

import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.service.SyncUserService;
import me.exrates.adminservice.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncUserServiceImpl implements SyncUserService {

    @Value("${sync.properties.users-chunk-size:100}")
    private int limit;

    private final AdminUserRepository adminUserRepository;
    private final CoreUserRepository coreUserRepository;

    @Autowired
    public SyncUserServiceImpl(AdminUserRepository adminUserRepository,
                               CoreUserRepository coreUserRepository) {
        this.adminUserRepository = adminUserRepository;
        this.coreUserRepository = coreUserRepository;
    }

    @Override
    public void syncUsers() {
        boolean shouldProceed;
        do {
            final Integer maxUserId = adminUserRepository.findMaxUserId();
            final List<CoreUser> users = coreUserRepository.findAllAfterIdLimited(maxUserId, limit);
            shouldProceed = ! users.isEmpty();
            adminUserRepository.batchUpdate(users);
        } while (shouldProceed);
    }
}
