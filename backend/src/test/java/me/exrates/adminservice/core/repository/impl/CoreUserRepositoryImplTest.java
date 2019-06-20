package me.exrates.adminservice.core.repository.impl;

import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreUserRepositoryImplTest.InnerConfig.class
})
public class CoreUserRepositoryImplTest extends DataComparisonTest {

    @Autowired
    private CoreUserRepository coreUserRepository;

    @Test
    public void findAllAdmins() {
        final List<CoreUser> allAdmins = coreUserRepository.findAllAdmins();

        assertEquals(2, allAdmins.size());
        assertTrue(allAdmins.stream().allMatch(coreUser -> coreUser.getUserRole().equalsIgnoreCase("ADMINISTRATOR")));
    }

    @Test
    public void findById_whenOk() {
        final Optional<CoreUser> found = coreUserRepository.findById(1);
        assertTrue(found.isPresent());
    }

    @Test
    public void findById_whenNotFound() {
        final Optional<CoreUser> found = coreUserRepository.findById(10);
        assertFalse(found.isPresent());
    }

    @Test
    public void findByUsername_whenOk() {
        final Optional<CoreUser> found = coreUserRepository.findByUsername("admin@exrates.me");
        assertTrue(found.isPresent());
    }

    @Test
    public void findByUsername_whenNonFound() {
        final Optional<CoreUser> found = coreUserRepository.findByUsername("admin-rate@exrates.me");
        assertFalse(found.isPresent());
    }

    @Test
    public void findAllUsersIdAndEmail() {
        final Map<Integer, String> users = coreUserRepository.findAllUsersIdAndEmail();

        assertEquals(3, users.size());
        assertTrue(users.containsKey(1));
        assertEquals("admin@exrates.me", users.get(1));
    }

    @Configuration
    @Profile("test")
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_JDBC_OPS)
        private JdbcOperations coreJdbcOperations;

        @Override
        protected String getSchema() {
            return "CoreUserRepositoryImplTest";
        }

        @Bean
        CoreUserRepository coreUserRepository() {
            return new CoreUserRepositoryImpl(coreNPJdbcOperations, coreJdbcOperations);
        }
    }
}