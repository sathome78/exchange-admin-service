package me.exrates.adminservice.core.repository.impl;

import config.AbstractDatabaseContextTest;
import config.DataComparisonTest;
import me.exrates.adminservice.core.domain.CoreUser;
import me.exrates.adminservice.core.domain.CoreUserOperationAuthorityOptionDto;
import me.exrates.adminservice.core.domain.FilterDto;
import me.exrates.adminservice.core.domain.UserBalancesInfoDto;
import me.exrates.adminservice.core.domain.UserDashboardDto;
import me.exrates.adminservice.core.domain.UserInfoDto;
import me.exrates.adminservice.core.domain.enums.UserOperationAuthority;
import me.exrates.adminservice.core.domain.enums.UserRole;
import me.exrates.adminservice.core.exceptions.UserNotFoundException;
import me.exrates.adminservice.core.exceptions.UserRoleNotFoundException;
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

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        CoreUserRepositoryImplTest.InnerConfig.class
})
public class CoreUserRepositoryImplTest extends DataComparisonTest {

    @Autowired
    @Qualifier("testCoreUserRepository")
    private CoreUserRepository coreUserRepository;

    @Override
    protected void before() {
        setDatabaseType(CORE_DATABASE_TYPE);
        try {
            truncateTables(CoreUserRepository.TABLE_NAME_1);

            String sql1 = "INSERT INTO " + CoreUserRepository.TABLE_NAME_1
                    + " (user_id, user_operation_id, enabled) VALUES (1, 1, false);";
            String sql2 = "UPDATE " + CoreUserRepository.TABLE_NAME
                    + " SET roleid = 1 WHERE id = 1";
            prepareTestData(sql1, sql2);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void findAllAdmins() {
        final List<CoreUser> allAdmins = coreUserRepository.findAllAdmins();

        assertEquals(1, allAdmins.size());
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

    @Test
    public void getIdByEmail_ok() {
        Integer userId = coreUserRepository.getIdByEmail("admin@exrates.me");

        assertNotNull(userId);
        assertEquals(1, userId.intValue());
    }

    @Test(expected = UserNotFoundException.class)
    public void getIdByEmail_not_found() {
        coreUserRepository.getIdByEmail("admin2@exrates.me");
    }

    @Test
    public void getUserRoleById_ok() {
        UserRole userRole = coreUserRepository.getUserRoleById(1);

        assertNotNull(userRole);
        assertEquals(UserRole.ADMINISTRATOR, userRole);
    }

    @Test(expected = UserRoleNotFoundException.class)
    public void getUserRoleById_not_found() {
        coreUserRepository.getUserRoleById(0);
    }

    @Test
    public void getUsersDashboard_ok() {
        UserDashboardDto usersDashboard = coreUserRepository.getUsersDashboard();

        assertNotNull(usersDashboard);
        assertEquals(3, usersDashboard.getAllUsersCount().intValue());
        assertEquals(2, usersDashboard.getAllVerifiedUsersCount().intValue());
        assertEquals(1, usersDashboard.getAllBlockedUsersCount().intValue());
        assertEquals(0, usersDashboard.getAllOnlineUsersCount().intValue());
    }

    @Test
    public void getUserInfoListCount_ok() {
        Integer count = coreUserRepository.getUserInfoListCount(FilterDto.builder().build(), 15, 0);

        assertNotNull(count);
        assertEquals(1, count.intValue());
    }

    @Test
    public void getUserInfoList_ok() {
        List<UserInfoDto> userInfoList = coreUserRepository.getUserInfoList(FilterDto.builder().build(), 15, 0);

        assertNotNull(userInfoList);
        assertFalse(userInfoList.isEmpty());
        assertEquals(1, userInfoList.size());
    }

    @Test
    public void getUserInfo_ok() {
        UserInfoDto userInfo = coreUserRepository.getUserInfo(1);

        assertNotNull(userInfo);
        assertEquals(1, userInfo.getUserId());
    }

    @Test(expected = RuntimeException.class)
    public void getUserInfo_not_found() {
        coreUserRepository.getUserInfo(0);
    }

    @Test
    public void getUserBalancesInfoListCount_ok() {
        Integer count = coreUserRepository.getUserBalancesInfoListCount(1, false, Collections.singletonList("BTC"));

        assertNotNull(count);
        assertEquals(1, count.intValue());
    }

    @Test
    public void getUserBalancesInfoListCount_not_found() {
        Integer count = coreUserRepository.getUserBalancesInfoListCount(1, true, Collections.singletonList("UAH"));

        assertNotNull(count);
        assertEquals(0, count.intValue());
    }

    @Test
    public void getUserBalancesInfoList_ok() {
        List<UserBalancesInfoDto> userBalancesInfoList = coreUserRepository.getUserBalancesInfoList(1, false, Collections.singletonList("BTC"), 15, 0);

        assertNotNull(userBalancesInfoList);
        assertFalse(userBalancesInfoList.isEmpty());
        assertEquals(1, userBalancesInfoList.size());
    }

    @Test
    public void getUserBalancesInfoList_not_found() {
        List<UserBalancesInfoDto> userBalancesInfoList = coreUserRepository.getUserBalancesInfoList(0, false, Collections.singletonList("BTC"), 15, 0);

        assertNotNull(userBalancesInfoList);
        assertTrue(userBalancesInfoList.isEmpty());
    }

    @Test
    public void updateUserOperationAuthority_ok() {
        CoreUserOperationAuthorityOptionDto userAuthorityOption = CoreUserOperationAuthorityOptionDto.builder()
                .userOperationAuthority(UserOperationAuthority.INPUT)
                .enabled(true)
                .build();

        around()
                .withSQL("SELECT * FROM " + CoreUserRepository.TABLE_NAME_1)
                .run(() -> coreUserRepository.updateUserOperationAuthority(Collections.singletonList(userAuthorityOption), 1));
    }

    @Test
    public void getUserOperationTypeAuthorities_ok() {
        List<CoreUserOperationAuthorityOptionDto> authorities = coreUserRepository.getUserOperationTypeAuthorities(1);

        assertNotNull(authorities);
        assertFalse(authorities.isEmpty());
        assertEquals(1, authorities.size());
    }

    @Test
    public void getUserOperationTypeAuthorities_not_found() {
        List<CoreUserOperationAuthorityOptionDto> authorities = coreUserRepository.getUserOperationTypeAuthorities(0);

        assertNotNull(authorities);
        assertTrue(authorities.isEmpty());
    }

    @Test
    public void updateUserRole_ok() {
        around()
                .withSQL("SELECT * FROM " + CoreUserRepository.TABLE_NAME)
                .run(() -> coreUserRepository.updateUserRole(UserRole.USER, 1));
    }

    @Test
    public void getAllRoles_ok() {
        List<UserRole> roles = coreUserRepository.getAllRoles();

        assertNotNull(roles);
        assertFalse(roles.isEmpty());
        assertEquals(12, roles.size());
    }

    @Configuration
    @Profile("test")
    static class InnerConfig extends AbstractDatabaseContextTest.AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE) // it's ok bean will be imported later
        private NamedParameterJdbcOperations coreNPJdbcOperations;

        @Autowired
        @Qualifier(TEST_CORE_TEMPLATE)
        private JdbcOperations coreJdbcOperations;

        @Override
        protected String getSchema() {
            return "CoreUserRepositoryImplTest";
        }

        @Bean("testCoreUserRepository")
        CoreUserRepository coreUserRepository() {
            return new CoreUserRepositoryImpl(coreNPJdbcOperations, coreJdbcOperations);
        }
    }
}