package me.exrates.adminservice.core.repository.impl;

import com.google.common.collect.ImmutableList;
import config.DataComparisonTest;
import me.exrates.adminservice.core.repository.IpLogRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        IpLogRepositoryImplTest.InnerConfig.class
})
public class IpLogRepositoryImplTest extends DataComparisonTest {

    @Autowired
    private IpLogRepository ipLogRepository;

    @Test
    public void findAllByUserIds() {
        Collection<Integer> userIds = ImmutableList.of(1, 2, 66);
        final Map<Integer, LocalDateTime> allByUserIds = ipLogRepository.findAllByUserIds(userIds);

        assertTrue(allByUserIds.get(1).isAfter(LocalDateTime.now().minusMinutes(1450)));
        assertTrue(allByUserIds.get(2).isAfter(LocalDateTime.now().minusMinutes(125)));

        assertNull(allByUserIds.get(66));
    }

    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE)
        private NamedParameterJdbcOperations namedParameterJdbcOperations;

        @Override
        protected String getSchema() {
            return "IpLogRepositoryImplTest";
        }

        @Bean
        IpLogRepository ipLogRepository() {
            return new IpLogRepositoryImpl(namedParameterJdbcOperations);
        }

    }
}
