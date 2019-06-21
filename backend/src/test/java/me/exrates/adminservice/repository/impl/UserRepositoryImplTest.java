package me.exrates.adminservice.repository.impl;

import com.google.common.collect.ImmutableList;
import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.domain.User;
import me.exrates.adminservice.repository.UserRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        UserRepositoryImplTest.InnerConfig.class
})
public class UserRepositoryImplTest extends DataComparisonTest {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void before() {
        setDatabaseType(ADMIN_DATABASE_TYPE);
    }

    @Test
    public void findOne() {
        final String username = "admin@exrates.me";
        final Optional<User> admin = userRepository.findOne(username);

        assertTrue(admin.isPresent());
        assertEquals(username, admin.get().getUsername());
    }

    @Test
    public void batchUpdate() {
        around()
                .withSQL("SELECT * FROM " + UserRepository.TABLE)
                .run(() -> userRepository.batchUpdate(getTestCoruUsers()));
    }

    private List<CoreUser> getTestCoruUsers() {
        CoreUser vasya = CoreUser.builder()
                .userId(1)
                .publicId("HGFHGFH")
                .email("test@email.com")
                .password("jhdfgskjfgska")
                .regdate(LocalDateTime.now())
                .phone("02")
                .userStatus("ACTIVE")
                .userRole("ADMINISTRATOR")
                .use2fa(true)
                .kycStatus("success")
                .build();
        CoreUser petya = CoreUser.builder()
                .userId(2)
                .publicId("HGFHGFH")
                .email("test2@email.com")
                .password("jhdfgskjfgska")
                .regdate(LocalDateTime.now())
                .phone("02")
                .userStatus("ACTIVE")
                .userRole("ADMINISTRATOR")
                .use2fa(true)
                .kycStatus("success")
                .build();
        return ImmutableList.of(vasya, petya);
    }

    @Configuration
    @Profile("test")
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_ADMIN_NP_TEMPLATE)
        private NamedParameterJdbcOperations namedParameterJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_JDBC_OPS)
        private JdbcOperations jdbcOperations;

        @Override
        protected String getSchema() {
            return "UserRepositoryImplTest";
        }

        @Bean
        UserRepository userRepository() {
            return new UserRepositoryImpl(namedParameterJdbcOperations, jdbcOperations);
        }
    }
}
