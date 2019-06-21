package me.exrates.adminservice.core.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreCommissionDto;
import me.exrates.adminservice.core.domain.enums.OperationType;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.core.exceptions.CommissionsNotFoundException;
import me.exrates.adminservice.core.repository.CoreCommissionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreCommissionRepositoryImplTest.InnerConfig.class
})
public class CoreCommissionRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier("testCoreCommissionRepository")
    private CoreCommissionRepository coreCommissionRepository;

    @Override
    protected void before() {
        setDatabaseType(CORE_DATABASE_TYPE);
    }

    @Test
    public void getCommission_ok() {
        CoreCommissionDto commission = coreCommissionRepository.getCommission(OperationType.INPUT, UserRole.ADMINISTRATOR);

        assertNotNull(commission);
    }

    @Test(expected = CommissionsNotFoundException.class)
    public void getCommission_not_found() {
        coreCommissionRepository.getCommission(OperationType.OUTPUT, UserRole.ADMINISTRATOR);
    }

    @Configuration
    @Profile("test")
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Override
        protected String getSchema() {
            return "CoreCommissionRepositoryImplTest";
        }

        @Bean("testCoreCommissionRepository")
        CoreCommissionRepository coreCommissionRepository() {
            return new CoreCommissionRepositoryImpl(coreNPJdbcOperations);
        }
    }
}