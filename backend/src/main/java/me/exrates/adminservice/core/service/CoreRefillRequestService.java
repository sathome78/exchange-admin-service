package me.exrates.adminservice.core.service;

import me.exrates.adminservice.domain.enums.OperationPeriodEnum;

import java.util.Collection;
import java.util.Map;

public interface CoreRefillRequestService {

    int countNonRefilledCoins(Map<Integer, Map<OperationPeriodEnum, Integer>> data, int userId, OperationPeriodEnum period);

    Map<Integer, Map<OperationPeriodEnum, Integer>> findAllAddressesByUserIds(Collection<Integer> userIds);
}
