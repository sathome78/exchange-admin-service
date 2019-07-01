package me.exrates.adminservice.core.service.impl;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.exceptions.CommonAPIException;
import me.exrates.adminservice.core.repository.CoreRefillRequestRepository;
import me.exrates.adminservice.core.service.CoreRefillRequestService;
import me.exrates.adminservice.domain.enums.ApiErrorsEnum;
import me.exrates.adminservice.domain.enums.OperationPeriodEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.exrates.adminservice.domain.enums.OperationPeriodEnum.LAST_2_DAYS;
import static me.exrates.adminservice.domain.enums.OperationPeriodEnum.LAST_30_DAYS;
import static me.exrates.adminservice.domain.enums.OperationPeriodEnum.LAST_7_DAYS;
import static me.exrates.adminservice.domain.enums.OperationPeriodEnum.LAST_90_DAYS;
import static me.exrates.adminservice.domain.enums.OperationPeriodEnum.getBound;

@Service
@Log4j2
public class CoreRefillRequestServiceImpl implements CoreRefillRequestService {

    private final CoreRefillRequestRepository coreRefillRequestRepository;

    @Autowired
    public CoreRefillRequestServiceImpl(CoreRefillRequestRepository coreRefillRequestRepository) {
        this.coreRefillRequestRepository = coreRefillRequestRepository;
    }

    @Override
    public int countNonRefilledCoins(Map<Integer, Map<OperationPeriodEnum, Integer>> data, int userId, OperationPeriodEnum period) {
        if (! data.containsKey(userId)) {
            final String message = "Failed processing, as userId: " + userId + " not specified in requested ids: " + data.keySet();
            log.error(message);
            throw new CommonAPIException(ApiErrorsEnum.UNREQUESTED_USER_ERROR, message);
        }
        return data.get(userId).getOrDefault(period, 0);
    }

    @Override
    public Map<Integer, Map<OperationPeriodEnum, Integer>> findAllAddressesByUserIds(Collection<Integer> userIds) {
        final List<Pair<Integer, LocalDateTime>> unpaidAddressesByUserIds = coreRefillRequestRepository.findGeneratedUnpaidAddressesByUserIds(userIds);
        Map<Integer, Map<OperationPeriodEnum, Integer>> results = new HashMap<>(userIds.size());
        userIds.forEach(userId -> results.put(userId, getCoinsByPeriods(userId, unpaidAddressesByUserIds)));
        return results;
    }

    private Map<OperationPeriodEnum, Integer> getCoinsByPeriods(int userId, List<Pair<Integer, LocalDateTime>> values) {
        final List<LocalDateTime> userDates = values.stream()
                .filter(p -> p.getKey() == userId)
                .map(Pair::getRight)
                .collect(Collectors.toList());
        Map<OperationPeriodEnum, Integer> results = new HashMap<>();
        ImmutableList.of(LAST_2_DAYS, LAST_7_DAYS, LAST_30_DAYS, LAST_90_DAYS)
                .forEach(period -> results.put(period, countRecords(userDates, period)));
        return results;
    }

    private int countRecords(List<LocalDateTime> filteredDates, OperationPeriodEnum period) {
        return (int) filteredDates.stream()
                .filter(value -> value.isEqual(getBound(period)) || value.isAfter(getBound(period))).count();
    }

}
