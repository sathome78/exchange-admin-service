package me.exrates.adminservice.services.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.UserInoutStatus;
import me.exrates.adminservice.domain.UserInsight;
import me.exrates.adminservice.domain.api.UserInsightDTO;
import me.exrates.adminservice.repository.UserInoutStatusRepository;
import me.exrates.adminservice.repository.UserInsightRepository;
import me.exrates.adminservice.services.UserInsightsService;
import me.exrates.adminservice.utils.UserInsightMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserInsightsServiceImpl implements UserInsightsService {

    private final CoreUserRepository coreUserRepository;
    private final UserInsightRepository userInsightRepository;
    private final UserInoutStatusRepository userInoutStatusRepository;
    private final LoadingCache<Integer, Set<UserInsight>> insightsCache;

    @Autowired
    public UserInsightsServiceImpl(CoreUserRepository coreUserRepository,
                                   UserInsightRepository userInsightRepository,
                                   UserInoutStatusRepository userInoutStatusRepository) {
        this.coreUserRepository = coreUserRepository;
        this.userInsightRepository = userInsightRepository;
        this.userInoutStatusRepository = userInoutStatusRepository;
        this.insightsCache = buildCache();
    }

    public UserInsightsServiceImpl(CoreUserRepository coreUserRepository,
                                   UserInsightRepository userInsightRepository,
                                   UserInoutStatusRepository userInoutStatusRepository,
                                   LoadingCache<Integer, Set<UserInsight>> insightsCache) {
        this.coreUserRepository = coreUserRepository;
        this.userInsightRepository = userInsightRepository;
        this.userInoutStatusRepository = userInoutStatusRepository;
        this.insightsCache = insightsCache;
    }

    @PostConstruct
    public void loadData() {
        reloadCache(Collections.emptySet());
    }

    @Override
    public PagedResult<UserInsightDTO> findAll(int limit, int offset) {
        final Set<Integer> activeUserIds = userInsightRepository.getActiveUserIds(limit, offset);
        boolean hasNextPage = limit < 1 ? activeUserIds.size() > 20 : activeUserIds.size() > limit;
        final Map<Integer, Set<UserInsight>> storedInsights = insightsCache.getAllPresent(activeUserIds);
        final Map<Integer, UserInoutStatus> allBalances = userInoutStatusRepository.findAll(new ArrayList<>(activeUserIds));
        final Map<Integer, String> allUsersIdAndEmail = coreUserRepository.findAllUsersIdAndEmail();
        final Map<Integer, UserInsightDTO> results = new HashMap<>();
        activeUserIds.forEach(id -> {
            UserInsightDTO dto;
            final String email = allUsersIdAndEmail.getOrDefault(id, "not found");
            if (storedInsights.containsKey(id)) {
                final Set<UserInsight> userInsights = storedInsights.get(id);
                dto = UserInsightMapper.map(id, userInsights);
                dto.setEmail(email);
                UserInsightMapper.calculate(dto, allBalances);
            } else {
                dto = UserInsightDTO.empty(id, email);
            }
            results.put(id, dto);
        });
        return new PagedResult<>(hasNextPage, results.values());
    }

    @Override
    public PagedResult<UserInsightDTO> findAll(String username) {
        return getUserInsightDTOPagedResult(coreUserRepository.findByUsername(username));
    }

    @Override
    public PagedResult<UserInsightDTO> findAll(int userId) {
        return getUserInsightDTOPagedResult(coreUserRepository.findById(userId));
    }

    @Override
    public void reloadCache(Set<Integer> userIds) {
        insightsCache.putAll(readAll(userIds));
    }

    private LoadingCache<Integer, Set<UserInsight>> buildCache() {
        return Caffeine.newBuilder()
                .refreshAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<Integer, Set<UserInsight>>() {
                    @CheckForNull
                    @Override
                    public Set<UserInsight> load(@Nonnull Integer userId) {
                        return userInsightRepository.findAllByUserId(userId);
                    }

                    @CheckForNull
                    @Override
                    public Set<UserInsight> reload(@Nonnull Integer key, @Nonnull Set<UserInsight> oldValue) {
                        return userInsightRepository.findAllByUserId(key);
                    }
                });
    }

    private Map<Integer, Set<UserInsight>> readAll(Set<Integer> userIds) {
        int chunkSize = 100;
        int offset = 0;
        Map<Integer, Set<UserInsight>> data = new HashMap<>();
        final List<UserInsight> userInsights = userInsightRepository.findAll(chunkSize, offset, userIds);
        while (!userInsights.isEmpty()) {
            userInsights.forEach(userInsight -> {
                if (data.containsKey(userInsight.getUserId())) {
                    data.get(userInsight.getUserId()).add(userInsight);
                } else {
                    data.put(userInsight.getUserId(), new HashSet<>(ImmutableList.of(userInsight)));
                }
            });
            offset += chunkSize;
        }
        return data;
    }

    private PagedResult<UserInsightDTO> getUserInsightDTOPagedResult(Optional<CoreUser> coreUser) {
        PagedResult<UserInsightDTO> pagedResult = new PagedResult<>(false, new ArrayList<>());
        coreUser.ifPresent(insight -> {
            Set<UserInsight> storedInsights = insightsCache.get(insight.getUserId());
            if (Objects.isNull(storedInsights)) {
                storedInsights = new HashSet<>();
            }
            final UserInsightDTO insightDTO = UserInsightMapper.map(insight.getUserId(), storedInsights);
            insightDTO.setEmail(coreUserRepository.findAllUsersIdAndEmail().getOrDefault(insight.getUserId(), "not found"));
            UserInsightMapper.calculate(insightDTO, userInoutStatusRepository.findAll(ImmutableList.of(insight.getUserId())));
            pagedResult.getItems().add(insightDTO);
        });
        return pagedResult;
    }
}
