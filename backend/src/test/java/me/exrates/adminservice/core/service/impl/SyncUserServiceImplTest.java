package me.exrates.adminservice.core.service.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.repository.impl.CoreUserRepositoryImpl;
import me.exrates.adminservice.core.service.SyncUserService;
import me.exrates.adminservice.repository.AdminUserRepository;
import me.exrates.adminservice.repository.impl.AdminUserRepositoryImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        SyncUserServiceImplTest.InnerConfig.class
})
public class SyncUserServiceImplTest extends DataComparisonTest {

    private final String TABLE = "USERS";

    @Autowired
    private SyncUserService syncUserService;

    @Override
    protected void before() {
        try {
            truncateTables(TABLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void syncUsers() {
        around()
                .withSQL("SELECT * FROM "+ TABLE)
                .run(() -> syncUserService.syncUsers());
    }

    @Configuration
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_ADMIN_NP_TEMPLATE)
        protected NamedParameterJdbcOperations adminNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE)
        protected NamedParameterJdbcOperations coreNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_JDBC_OPS)
        protected JdbcOperations adminJdbcOperations;

        @Bean
        public CoreUserRepository coreUserRepository() {
            return new CoreUserRepositoryImpl(coreNPJdbcOperations);
        }

        @Bean
        public AdminUserRepository adminUserRepository() {
            return new AdminUserRepositoryImpl(adminNPJdbcOperations, adminJdbcOperations);
        }

        @Bean
        public SyncUserService syncUserService() {
            return new SyncUserServiceImpl(adminUserRepository(), coreUserRepository());
        }

        @Override
        protected String getSchema() {
            return "SyncUserServiceImplTest";
        }
    }
}
