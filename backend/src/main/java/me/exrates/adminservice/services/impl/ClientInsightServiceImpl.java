package me.exrates.adminservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.ClientInsightDTO;
import me.exrates.adminservice.repository.UserInsightRepository;
import me.exrates.adminservice.services.ClientInsightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Log4j2
public class ClientInsightServiceImpl implements ClientInsightService {

    private final UserInsightRepository userInsightRepository;

    @Autowired
    public ClientInsightServiceImpl(UserInsightRepository userInsightRepository) {
        this.userInsightRepository = userInsightRepository;
    }


    @Override
    public PagedResult<ClientInsightDTO> findAll(int limit, int offset) {
        final Set<Integer> activeUserIds = userInsightRepository.getActiveUserIds(limit, offset);
        boolean hasNextPage = limit < 1 ? activeUserIds.size() > 20 : activeUserIds.size() > limit;




        return null;
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
