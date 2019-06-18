package me.exrates.adminservice.services.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.repository.impl.CoreUserRepositoryImpl;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.UserInsightDTO;
import me.exrates.adminservice.repository.UserInoutStatusRepository;
import me.exrates.adminservice.repository.UserInsightRepository;
import me.exrates.adminservice.repository.impl.UserInoutStatusRepositoryImpl;
import me.exrates.adminservice.repository.impl.UserInsightRepositoryImpl;
import me.exrates.adminservice.services.UserInsightsService;
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

import java.sql.SQLException;

import static me.exrates.adminservice.repository.UserInsightRepository.TABLE;
import static me.exrates.adminservice.repository.impl.UserInsightRepositoryImplTest.getInsertData;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        UserInsightsServiceImplTest.InnerConfig.class
})
public class UserInsightsServiceImplTest extends DataComparisonTest {

    @Autowired
    private UserInsightsService userInsightsService;

    @Override
    protected void before() {
        try {
            truncateTables(TABLE);
            prepareTestData(getInsertData());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findAll_byUsername() {
        final PagedResult<UserInsightDTO> insightDTOPagedResult = userInsightsService.findAll("hi");
        assertEquals(1, insightDTOPagedResult.getItems().size());
    }

    @Test
    public void findAll_byUserId() {
    }

    @Test
    public void findAll() {

    }

    @Configuration
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE)
        private NamedParameterJdbcOperations coreParameterJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_NP_TEMPLATE)
        private NamedParameterJdbcOperations adminParameterJdbcOperations;

        @Override
        protected String getSchema() {
            return "UserInsightsServiceImplTest";
        }

        @Bean
        CoreUserRepository coreUserRepository() {
            return new CoreUserRepositoryImpl(coreParameterJdbcOperations);
        }

        @Bean
        UserInsightRepository userInsightRepository() {
            return new UserInsightRepositoryImpl(adminParameterJdbcOperations);
        }

        @Bean
        UserInoutStatusRepository userInoutStatusRepository() {
            return new UserInoutStatusRepositoryImpl(adminParameterJdbcOperations);
        }

        @Bean
        UserInsightsService userInsightsService() {
            return new UserInsightsServiceImpl(coreUserRepository(), userInsightRepository(), userInoutStatusRepository());
        }
    }
}
