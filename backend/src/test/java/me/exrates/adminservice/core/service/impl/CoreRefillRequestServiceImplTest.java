package me.exrates.adminservice.core.service.impl;

import com.google.common.collect.ImmutableList;
import config.DataComparisonTest;
import me.exrates.adminservice.core.exceptions.CommonAPIException;
import me.exrates.adminservice.core.repository.CoreRefillRequestRepository;
import me.exrates.adminservice.core.repository.impl.CoreRefillRequestRepositoryImpl;
import me.exrates.adminservice.core.service.CoreRefillRequestService;
import me.exrates.adminservice.domain.enums.RefillAddressEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreRefillRequestServiceImplTest.InnerConfig.class
})
public class CoreRefillRequestServiceImplTest extends DataComparisonTest {

    @Autowired
    private CoreRefillRequestService coreRefillRequestService;

    @Test
    public void testFindAllAddressesByUserIds () {
        final ImmutableList<Integer> userIds = ImmutableList.of(1, 2, 666);
        final Map<Integer, Map<RefillAddressEnum, Integer>> byUserIds = coreRefillRequestService.findAllAddressesByUserIds(userIds);

        assertEquals(userIds.size(), byUserIds.keySet().size());
        assertEquals(12, byUserIds.values().stream().mapToLong(p -> p.values().size()).sum());
    }

    @Test
    public void countNonRefilledCoins() {
        final ImmutableList<Integer> userIds = ImmutableList.of(1, 2, 666);
        final Map<Integer, Map<RefillAddressEnum, Integer>> byUserIds = coreRefillRequestService.findAllAddressesByUserIds(userIds);

        assertEquals(1, coreRefillRequestService.countNonRefilledCoins(byUserIds, 1, RefillAddressEnum.LAST_2_DAYS));
        assertEquals(2, coreRefillRequestService.countNonRefilledCoins(byUserIds, 1, RefillAddressEnum.LAST_7_DAYS));

        assertEquals(3, coreRefillRequestService.countNonRefilledCoins(byUserIds, 2, RefillAddressEnum.LAST_30_DAYS));
        assertEquals(4, coreRefillRequestService.countNonRefilledCoins(byUserIds, 2, RefillAddressEnum.LAST_90_DAYS));

        assertEquals(0, coreRefillRequestService.countNonRefilledCoins(byUserIds, 666, RefillAddressEnum.LAST_2_DAYS));
        assertEquals(0, coreRefillRequestService.countNonRefilledCoins(byUserIds, 666, RefillAddressEnum.LAST_90_DAYS));
    }

    @Test
    public void countNonRefilledCoins_withException() {
        final ImmutableList<Integer> userIds = ImmutableList.of(1, 2, 666);
        final Map<Integer, Map<RefillAddressEnum, Integer>> addressesByUserIds = coreRefillRequestService.findAllAddressesByUserIds(userIds);
        try {
            coreRefillRequestService.countNonRefilledCoins(addressesByUserIds, 77, RefillAddressEnum.LAST_30_DAYS);
            fail();
        } catch (RuntimeException e) {
            assertTrue(e instanceof CommonAPIException);
            CommonAPIException exception = (CommonAPIException) e;
            String expected = "Failed processing, as userId: 77 not specified in requested ids: [1, 2, 666]";
            assertEquals(expected, exception.getMessage());
        }
    }

    @Configuration
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE)
        protected NamedParameterJdbcOperations coreNPJdbcOperations;

        @Bean
        CoreRefillRequestRepository coreRefillRequestRepository() {
            return new CoreRefillRequestRepositoryImpl(coreNPJdbcOperations);
        }

        @Bean
        public CoreRefillRequestService coreRefillRequestService() {
            return new CoreRefillRequestServiceImpl(coreRefillRequestRepository());
        }

        @Override
        protected String getSchema() {
            return "CoreRefillRequestServiceImplTest";
        }
    }
}
