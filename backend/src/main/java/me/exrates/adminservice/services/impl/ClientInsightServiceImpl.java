package me.exrates.adminservice.services.impl;

import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.repository.IpLogRepository;
import me.exrates.adminservice.core.service.CoreRefillRequestService;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.ClientInsightDTO;
import me.exrates.adminservice.domain.enums.RefillAddressEnum;
import me.exrates.adminservice.domain.enums.RefillEventEnum;
import me.exrates.adminservice.repository.UserInsightRepository;
import me.exrates.adminservice.services.ClientInsightService;
import me.exrates.adminservice.services.OrderService;
import me.exrates.adminservice.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Log4j2
public class ClientInsightServiceImpl implements ClientInsightService {

    private final CoreRefillRequestService coreRefillRequestService;
    private final CoreUserRepository coreUserRepository;
    private final IpLogRepository ipLogRepository;
    private final OrderService orderService;
    private final TransactionService transactionService;
    private final UserInsightRepository userInsightRepository;

    @Autowired
    public ClientInsightServiceImpl(UserInsightRepository userInsightRepository,
                                    CoreUserRepository coreUserRepository,
                                    CoreRefillRequestService coreRefillRequestService,
                                    IpLogRepository ipLogRepository,
                                    OrderService orderService,
                                    TransactionService transactionService) {
        this.userInsightRepository = userInsightRepository;
        this.coreUserRepository = coreUserRepository;
        this.coreRefillRequestService = coreRefillRequestService;
        this.ipLogRepository = ipLogRepository;
        this.orderService = orderService;
        this.transactionService = transactionService;
    }

    @Override
    public PagedResult<ClientInsightDTO> findAll(int limit, int offset) {
        final Set<Integer> userIds = userInsightRepository.getActiveUserIds(limit, offset);
        boolean hasNextPage = limit < 1 ? userIds.size() > 20 : userIds.size() > limit;
        final Map<Integer, String> allUsersIdAndEmail = coreUserRepository.findAllUsersIdAndEmail();
        final Map<Integer, LocalDateTime> usersIps = ipLogRepository.findAllByUserIds(userIds);
        final Map<Integer, Map<RefillAddressEnum, Integer>> coinsCounter = coreRefillRequestService.findAllAddressesByUserIds(userIds);
        final Map<Integer, List<Integer>> usersRefills = transactionService.getAllUsersRefills(userIds);
        final Map<Integer, Set<RefillEventEnum>> refillEvents = transactionService.getAllUsersRefillEvents(userIds);
        final Map<Integer, List<Integer>> usersClosedOrders = orderService.getAllUserClosedOrders(userIds);

        List<ClientInsightDTO> insightDTOS = Lists.newArrayList();
        userIds.forEach(userId -> {
            ClientInsightDTO dto = new ClientInsightDTO();
            dto.setUserId(userId);
            dto.setEmail(allUsersIdAndEmail.getOrDefault(userId, ""));
            dto.setLastLogin(usersIps.get(userId));
            dto.setNotRefilledCoins48Hours(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, RefillAddressEnum.LAST_2_DAYS));
            dto.setNotRefilledCoins7Days(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, RefillAddressEnum.LAST_7_DAYS));
            dto.setNotRefilledCoins30Days(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, RefillAddressEnum.LAST_30_DAYS));
            dto.setNotRefilledCoins90Days(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, RefillAddressEnum.LAST_90_DAYS));
            final int refillsSize = usersRefills.getOrDefault(userId, Collections.emptyList()).size();
            dto.setFirstRefill(refillsSize == 1);
            dto.setTwinRefill(refillsSize == 2);
            dto.setRefillAndTrade(refillsSize > 0 && usersClosedOrders.get(userId).size() == 1);
            dto.setZeroedBalance(refillEvents.get(userId).contains(RefillEventEnum.ZEROED));
            dto.setReanimateAccount(refillEvents.get(userId).contains(RefillEventEnum.REANIMATED));






            insightDTOS.add(dto);
        });
        return new PagedResult<>(hasNextPage, insightDTOS);
    }

    @Override
    public PagedResult<ClientInsightDTO> findAll(String username) {
        return null;
    }

    @Override
    public PagedResult<ClientInsightDTO> findAll(int userId) {
        return null;
    }

    @Override
    public void reloadCache(Set userIds) {

    }

}
