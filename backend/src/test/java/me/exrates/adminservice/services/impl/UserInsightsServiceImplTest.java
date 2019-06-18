package me.exrates.adminservice.services.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.api.UserInsightDTO;
import me.exrates.adminservice.services.UserInsightsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        UserInsightsServiceImplTest.InnerConfig.class
})
public class UserInsightsServiceImplTest extends DataComparisonTest {

    @Autowired
    private UserInsightsService userInsightsService;

    @Test
    public void findAll_byUsername() {
        final PagedResult<UserInsightDTO> insightDTOPagedResult = userInsightsService.findAll();
    }

    @Test
    public void findAll1() {
    }

    @Test
    public void findAll2() {
    }

    @Configuration
    static class InnerConfig extends AppContextConfig {

        @Override
        protected String getSchema() {
            return "UserInsightsServiceImplTest";
        }



        @Bean
        UserInsightsService userInsightsService() {
            return new UserInsightsServiceImpl()
        }
    }
}
