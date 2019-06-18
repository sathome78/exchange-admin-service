package me.exrates.adminservice.core.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.UserOperationAuthorityOption;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.service.CoreUserService;
import me.exrates.adminservice.domain.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@Transactional
public class CoreUserServiceImpl implements CoreUserService {

    private final CoreUserRepository coreUserRepository;

    @Autowired
    public CoreUserServiceImpl(CoreUserRepository coreUserRepository) {
        this.coreUserRepository = coreUserRepository;
    }

    @Override
    public void updateUserOperationAuthority(List<UserOperationAuthorityOption> options, Integer userId) {
        UserRole currentUserRole = coreUserRepository.getUserRoles(currentUserEmail);
        UserRole updatedUserRole = userDao.getUserRoleById(userId);
        if (currentUserRole != UserRole.ADMINISTRATOR && updatedUserRole == UserRole.ADMINISTRATOR) {
            throw new ForbiddenOperationException("Status modification not permitted");
        }
        userOperationDao.updateUserOperationAuthority(options, userId);
    }
}
