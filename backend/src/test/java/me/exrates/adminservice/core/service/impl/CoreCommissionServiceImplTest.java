package me.exrates.adminservice.core.service.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.core.exceptions.CommissionsNotFoundException;
import me.exrates.adminservice.core.repository.CoreCommissionRepository;
import me.exrates.adminservice.core.service.CoreCommissionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CoreCommissionServiceImplTest.InnerConfig.class)
public class CoreCommissionServiceImplTest {

    private static final String COMMISSION_CACHE_BY_ROLE_AND_OPERATION_TEST = "commission-cache-by-role-operation-test";

    @Mock
    private CoreCommissionRepository coreCommissionRepository;
    @Autowired
    @Qualifier(COMMISSION_CACHE_BY_ROLE_AND_OPERATION_TEST)
    private Cache commissionCacheByRoleAndType;

    private CoreCommissionService coreCommissionService;

    @Before
    public void setUp() throws Exception {
        final String cacheKey = String.join("-", UserRole.USER.name(), OperationType.INPUT.name());
        commissionCacheByRoleAndType.put(cacheKey, CoreCommissionDto.builder()
                .operationType(OperationType.INPUT)
                .userRole(UserRole.USER)
                .build());

        coreCommissionService = spy(new CoreCommissionServiceImpl(coreCommissionRepository, commissionCacheByRoleAndType));
    }

    @Test
    public void findCommissionByTypeAndRole_without_cache() {
        commissionCacheByRoleAndType.clear();

        doReturn(CoreCommissionDto.builder()
                .operationType(OperationType.INPUT)
                .userRole(UserRole.USER)
                .build())
                .when(coreCommissionRepository)
                .getCommission(any(OperationType.class), any(UserRole.class));

        CoreCommissionDto commission = coreCommissionService.findCachedCommissionByTypeAndRole(OperationType.INPUT, UserRole.USER);

        assertNotNull(commission);
        assertEquals(OperationType.INPUT, commission.getOperationType());
        assertEquals(UserRole.USER, commission.getUserRole());

        verify(coreCommissionRepository, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
    }

    @Test
    public void findCommissionByTypeAndRole_with_cache() {
        CoreCommissionDto commission = coreCommissionService.findCachedCommissionByTypeAndRole(OperationType.INPUT, UserRole.USER);

        assertNotNull(commission);
        assertEquals(OperationType.INPUT, commission.getOperationType());
        assertEquals(UserRole.USER, commission.getUserRole());

        verify(coreCommissionRepository, never()).getCommission(any(OperationType.class), any(UserRole.class));
    }

    @Test(expected = Cache.ValueRetrievalException.class)
    public void findCommissionByTypeAndRole_not_found() {
        commissionCacheByRoleAndType.clear();

        doThrow(CommissionsNotFoundException.class)
                .when(coreCommissionRepository)
                .getCommission(any(OperationType.class), any(UserRole.class));

        coreCommissionService.findCachedCommissionByTypeAndRole(OperationType.INPUT, UserRole.USER);
    }

    @Configuration
    static class InnerConfig {

        @Bean(COMMISSION_CACHE_BY_ROLE_AND_OPERATION_TEST)
        public Cache commissionCacheByRoleAndOperation() {
            return new CaffeineCache(COMMISSION_CACHE_BY_ROLE_AND_OPERATION_TEST, Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build());
        }
    }
}