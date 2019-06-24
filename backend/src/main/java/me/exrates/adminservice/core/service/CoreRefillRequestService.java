package me.exrates.adminservice.core.service;

import me.exrates.adminservice.domain.enums.RefillAddressEnum;

import java.util.Collection;
import java.util.Map;

public interface CoreRefillRequestService {

    int countNonRefilledCoins(Map<Integer, Map<RefillAddressEnum, Integer>> data, int userId, RefillAddressEnum period);

    Map<Integer, Map<RefillAddressEnum, Integer>> findAllAddressesByUserIds(Collection<Integer> userIds);
}
