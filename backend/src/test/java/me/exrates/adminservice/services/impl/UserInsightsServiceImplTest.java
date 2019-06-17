package me.exrates.adminservice.services.impl;

import config.DataComparisonTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    public void findAll() {
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
    }
}
