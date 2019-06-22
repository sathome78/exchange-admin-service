package me.exrates.adminservice.core.service;

import me.exrates.adminservice.domain.enums.RefillAddressEnum;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface CoreRefillRequestService {

    boolean hasUnrefilledAccounts(Map<Integer, Set<RefillAddressEnum>> data, int userId, RefillAddressEnum period);

    Map<Integer, Set<RefillAddressEnum>> findAllAddressesByUserIds(Collection<Integer> userIds);
}
