package me.exrates.adminservice.core.service.impl;

import me.exrates.adminservice.core.repository.CoreRefillRequestRepository;
import me.exrates.adminservice.core.service.CoreRefillRequestService;
import me.exrates.adminservice.domain.enums.RefillAddressEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Service
public class CoreRefillRequestServiceImpl implements CoreRefillRequestService {

    private final CoreRefillRequestRepository coreRefillRequestRepository;

    @Autowired
    public CoreRefillRequestServiceImpl(CoreRefillRequestRepository coreRefillRequestRepository) {
        this.coreRefillRequestRepository = coreRefillRequestRepository;
    }

    @Override
    public Map<Integer, Set<RefillAddressEnum>> findAllAddressesByUserIds(Collection<Integer> userIds) {


        return null;
    }
}
