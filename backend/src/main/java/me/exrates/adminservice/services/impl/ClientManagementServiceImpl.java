package me.exrates.adminservice.services.impl;

import me.exrates.adminservice.core.service.CoreUserService;
import me.exrates.adminservice.domain.api.ClientManagementBoardDTO;
import me.exrates.adminservice.services.ClientManagementService;
import me.exrates.adminservice.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ClientManagementServiceImpl implements ClientManagementService {

    private final CoreUserService coreUserService;
    private final TransactionService transactionService;

    @Autowired
    public ClientManagementServiceImpl(CoreUserService coreUserService,
                                       TransactionService transactionService) {
        this.coreUserService = coreUserService;
        this.transactionService = transactionService;
    }

    @Override
    public ClientManagementBoardDTO getClientManagementBoard() {
        ClientManagementBoardDTO dto = new ClientManagementBoardDTO();

        final Set<Integer> allUserIds = coreUserService.countAllRegisteredUsers();
        final Set<Integer> newUserIds = coreUserService.findAllNewUserIds();
        transactionService.


        dto.setRegisteredUsersAll(allUserIds.size());
        dto.setRegisteredUsersNew(newUserIds.size());

        transactionService.findUsersNoRefills();

        return dto;
    }
}
