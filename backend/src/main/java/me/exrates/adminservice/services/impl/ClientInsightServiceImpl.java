package me.exrates.adminservice.services.impl;

import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.repository.IpLogRepository;
import me.exrates.adminservice.core.service.CoreRefillRequestService;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.ClientInsightDTO;
import me.exrates.adminservice.domain.enums.RefillAddressEnum;
import me.exrates.adminservice.repository.UserInsightRepository;
import me.exrates.adminservice.services.ClientInsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Log4j2
public class ClientInsightServiceImpl implements ClientInsightService {

    private final UserInsightRepository userInsightRepository;
    private final CoreUserRepository coreUserRepository;
    private final CoreRefillRequestService coreRefillRequestService;
    private final IpLogRepository ipLogRepository;

    @Autowired
    public ClientInsightServiceImpl(UserInsightRepository userInsightRepository,
                                    CoreUserRepository coreUserRepository,
                                    CoreRefillRequestService coreRefillRequestService,
                                    IpLogRepository ipLogRepository) {
        this.userInsightRepository = userInsightRepository;
        this.coreUserRepository = coreUserRepository;
        this.coreRefillRequestService = coreRefillRequestService;
        this.ipLogRepository = ipLogRepository;
    }

    @Override
    public PagedResult<ClientInsightDTO> findAll(int limit, int offset) {
        final Set<Integer> activeUserIds = userInsightRepository.getActiveUserIds(limit, offset);
        boolean hasNextPage = limit < 1 ? activeUserIds.size() > 20 : activeUserIds.size() > limit;
        final Map<Integer, String> allUsersIdAndEmail = coreUserRepository.findAllUsersIdAndEmail();
        final Map<Integer, LocalDateTime> usersIps = ipLogRepository.findAllByUserIds(activeUserIds);
        final Map<Integer, Map<RefillAddressEnum, Integer>> coinsCounter = coreRefillRequestService.findAllAddressesByUserIds(activeUserIds);

        List<ClientInsightDTO> insightDTOS = Lists.newArrayList();
        activeUserIds.forEach(userId -> {
            ClientInsightDTO dto = new ClientInsightDTO();
            dto.setUserId(userId);
            dto.setEmail(allUsersIdAndEmail.getOrDefault(userId, ""));
            dto.setLastLogin(usersIps.get(userId));
            dto.setNotRefilledCoins48Hours(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, RefillAddressEnum.LAST_2_DAYS));
            dto.setNotRefilledCoins7Days(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, RefillAddressEnum.LAST_7_DAYS));
            dto.setNotRefilledCoins30Days(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, RefillAddressEnum.LAST_30_DAYS));
            dto.setNotRefilledCoins90Days(coreRefillRequestService.countNonRefilledCoins(coinsCounter, userId, RefillAddressEnum.LAST_90_DAYS));





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
