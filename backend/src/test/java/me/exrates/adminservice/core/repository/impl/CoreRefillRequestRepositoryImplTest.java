package me.exrates.adminservice.core.repository.impl;

import com.google.common.collect.ImmutableList;
import config.DataComparisonTest;
import me.exrates.adminservice.core.repository.CoreRefillRequestRepository;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreRefillRequestRepositoryImplTest.InnerConfig.class
})
public class CoreRefillRequestRepositoryImplTest extends DataComparisonTest {

    @Autowired
    private CoreRefillRequestRepository coreRefillRequestRepository;

    @Test
    public void testGetRefillAddressGeneratedByUserIds() {
        final ImmutableList<Integer> userIds = ImmutableList.of(1, 2, 666);
        final Map<Integer, Integer> generatedByUserIds = coreRefillRequestRepository.getRefillAddressGeneratedByUserIds(userIds);

        assertEquals(userIds.size(), generatedByUserIds.size());
        assertEquals(4, (int)generatedByUserIds.get(1));
        assertEquals(4, (int)generatedByUserIds.get(2));
        assertEquals(0, (int)generatedByUserIds.get(666));
    }

    @Configuration
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Override
        protected String getSchema() {
            return "CoreRefillRequestRepositoryImplTest";
        }

        @Bean
        CoreRefillRequestRepository coreRefillRequestRepository() {
            return new CoreRefillRequestRepositoryImpl(coreNPJdbcOperations);
        }
    }
}
