package me.exrates.adminservice.services.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.UserInsight;
import me.exrates.adminservice.domain.api.UserInsightDTO;
import me.exrates.adminservice.repository.UserInsightRepository;
import me.exrates.adminservice.services.UserInsightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@Service
@Log4j2
public class UserInsightsServiceImpl implements UserInsightsService {

    private final UserInsightRepository userInsightRepository;
    private final LoadingCache<Integer, List<UserInsight>> insightsCache;

    @Autowired
    public UserInsightsServiceImpl(UserInsightRepository userInsightRepository) {
        this.userInsightRepository = userInsightRepository;
        this.insightsCache = buildCache();
    }

    @PostConstruct
    public void loadData() {
        IntStream.range(1, 3).forEach(i -> {

        });
    }

    @Override
    public PagedResult<UserInsightDTO> findAll(int limit, int offset) {
        throw new UnsupportedOperationException("not yet");
    }

    @Override
    public PagedResult<UserInsightDTO> findAll(String username) {
        throw new UnsupportedOperationException("not yet");
    }

    @Override
    public PagedResult<UserInsightDTO> findAll(int userId) {
        throw new UnsupportedOperationException("not yet");
    }

    private LoadingCache<Integer, List<UserInsight>> buildCache() {
        return Caffeine.newBuilder()
                .refreshAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<Integer, List<UserInsight>>() {
                    @CheckForNull
                    @Override
                    public List<UserInsight> load(@Nonnull Integer userId) throws Exception {
                        return userInsightRepository.findAllByUserId(userId);
                    }

                    @CheckForNull
                    @Override
                    public List<UserInsight> reload(@Nonnull Integer key, @Nonnull List<UserInsight> oldValue) throws Exception {
                        return userInsightRepository.findAllByUserId(key);
                    }
                });
    }
}
