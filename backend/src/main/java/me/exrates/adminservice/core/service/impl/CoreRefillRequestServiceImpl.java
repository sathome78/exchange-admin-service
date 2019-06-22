package me.exrates.adminservice.core.service.impl;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.exceptions.CommonAPIException;
import me.exrates.adminservice.core.repository.CoreRefillRequestRepository;
import me.exrates.adminservice.core.service.CoreRefillRequestService;
import me.exrates.adminservice.domain.enums.ApiErrorsEnum;
import me.exrates.adminservice.domain.enums.RefillAddressEnum;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static me.exrates.adminservice.domain.enums.RefillAddressEnum.LAST_2_DAYS;
import static me.exrates.adminservice.domain.enums.RefillAddressEnum.LAST_30_DAYS;
import static me.exrates.adminservice.domain.enums.RefillAddressEnum.LAST_7_DAYS;
import static me.exrates.adminservice.domain.enums.RefillAddressEnum.LAST_90_DAYS;

@Service
@Log4j2
public class CoreRefillRequestServiceImpl implements CoreRefillRequestService {

    private final CoreRefillRequestRepository coreRefillRequestRepository;

    @Autowired
    public CoreRefillRequestServiceImpl(CoreRefillRequestRepository coreRefillRequestRepository) {
        this.coreRefillRequestRepository = coreRefillRequestRepository;
    }

    @Override
    public boolean hasUnrefilledAccounts(Map<Integer, Set<RefillAddressEnum>> data, int userId, RefillAddressEnum period) {
        if (! data.containsKey(userId)) {
            final String message = "Failed processing, as userId: " + userId + " not specified in requested ids: " + data.keySet();
            log.error(message);
            throw new CommonAPIException(ApiErrorsEnum.UNREQUESTED_USER_ERROR, message);
        }
        return data.get(userId).contains(period);
    }

    @Override
    public Map<Integer, Set<RefillAddressEnum>> findAllAddressesByUserIds(Collection<Integer> userIds) {
        final List<Pair<Integer, LocalDateTime>> unpaidAddressesByUserIds = coreRefillRequestRepository.findGeneratedUnpaidAddressesByUserIds(userIds);
        Map<Integer, Set<RefillAddressEnum>> results = new HashMap<>(userIds.size());
        userIds.forEach(userId -> results.put(userId, getAddressesByPeriods(userId, unpaidAddressesByUserIds)));
        return results;
    }

    private Set<RefillAddressEnum> getAddressesByPeriods(int userId, List<Pair<Integer, LocalDateTime>> values) {
        final List<LocalDateTime> userDates = values.stream()
                .filter(p -> p.getKey() == userId)
                .map(Pair::getRight)
                .collect(Collectors.toList());
        Set<RefillAddressEnum> set = new HashSet<>();
        ImmutableList.of(LAST_2_DAYS, LAST_7_DAYS, LAST_30_DAYS, LAST_90_DAYS)
                .forEach(period -> addPeriodIfPresent(set, userDates, period));
        return set;
    }

    private void addPeriodIfPresent(Set<RefillAddressEnum> set, List<LocalDateTime> userDates, RefillAddressEnum period) {
        if (hasRecords(userDates, period)) {
            set.add(period);
        }
    }

    private boolean hasRecords(List<LocalDateTime> filteredDates, RefillAddressEnum period) {
        return filteredDates.stream()
                .anyMatch(value -> value.isEqual(getBound(period)) || value.isAfter(getBound(period)));
    }

    private LocalDateTime getBound(RefillAddressEnum period) {
        switch (period) {
            case LAST_2_DAYS:
                return LocalDateTime.now().minusDays(2);
            case LAST_7_DAYS:
                return LocalDateTime.now().minusDays(7);
            case LAST_30_DAYS:
                return LocalDateTime.now().minusDays(30);
            case LAST_90_DAYS:
                return LocalDateTime.now().minusDays(90);
        }
        throw new UnsupportedOperationException("RefillAddressEnum " + period + " not allowed");
    }

}
