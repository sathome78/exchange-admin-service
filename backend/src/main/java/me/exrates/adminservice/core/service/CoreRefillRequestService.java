package me.exrates.adminservice.core.service;

import me.exrates.adminservice.domain.enums.RefillAddressEnum;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface CoreRefillRequestService {

    Map<Integer, Set<RefillAddressEnum>> findAllAddressesByUserIds(Collection<Integer> userIds);
}
