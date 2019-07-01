package me.exrates.adminservice.core.service.impl;

import com.github.benmanes.caffeine.cache.Caffeine;
import me.exrates.adminservice.core.domain.CoreOrderDto;
import me.exrates.adminservice.core.repository.CoreOrderRepository;
import me.exrates.adminservice.core.service.CoreOrderService;
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CoreOrderServiceImplTest.InnerConfig.class)
public class CoreOrderServiceImplTest {

    private static final String ORDER_CACHE_BY_ID_TEST = "order-cache-by-id-test";

    @Mock
    private CoreOrderRepository coreOrderRepository;
    @Autowired
    @Qualifier(ORDER_CACHE_BY_ID_TEST)
    private Cache orderCacheById;

    private CoreOrderService coreOrderService;

    @Before
    public void setUp() throws Exception {
        orderCacheById.put(1, CoreOrderDto.builder().build());

        coreOrderService = spy(new CoreOrderServiceImpl(coreOrderRepository, orderCacheById));
    }

    @Test
    public void findCachedOrderById_without_cache() {
        orderCacheById.clear();

        doReturn(CoreOrderDto.builder().build())
                .when(coreOrderRepository)
                .findOrderById(anyInt());

        CoreOrderDto order = coreOrderService.findCachedOrderById(1);

        assertNotNull(order);

        verify(coreOrderRepository, atLeastOnce()).findOrderById(anyInt());
    }

    @Test
    public void findCachedOrderById_with_cache() {
        doReturn(CoreOrderDto.builder().build())
                .when(coreOrderRepository)
                .findOrderById(anyInt());

        CoreOrderDto order = coreOrderService.findCachedOrderById(1);

        assertNotNull(order);

        verify(coreOrderRepository, never()).findOrderById(anyInt());
    }

    @Configuration
    static class InnerConfig {

        @Bean(ORDER_CACHE_BY_ID_TEST)
        public Cache orderCacheById() {
            return new CaffeineCache(ORDER_CACHE_BY_ID_TEST, Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .build());
        }
    }
}