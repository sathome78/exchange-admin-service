package me.exrates.adminservice.services.impl;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import config.DataComparisonTest;
import me.exrates.adminservice.core.repository.CoreUserRepository;
import me.exrates.adminservice.core.repository.impl.CoreUserRepositoryImpl;
import me.exrates.adminservice.domain.PagedResult;
import me.exrates.adminservice.domain.UserInsight;
import me.exrates.adminservice.domain.api.UserInsightDTO;
import me.exrates.adminservice.repository.UserInoutStatusRepository;
import me.exrates.adminservice.repository.UserInsightRepository;
import me.exrates.adminservice.repository.impl.UserInoutStatusRepositoryImpl;
import me.exrates.adminservice.repository.impl.UserInsightRepositoryImpl;
import me.exrates.adminservice.services.InsightService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static me.exrates.adminservice.repository.UserInsightRepository.TABLE;
import static me.exrates.adminservice.repository.impl.UserInsightRepositoryImplTest.getInsertData;
import static me.exrates.adminservice.repository.impl.UserInsightRepositoryImplTest.getTestUserInsights;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        UserInsightServiceImplTest.InnerConfig.class
})
public class UserInsightServiceImplTest extends DataComparisonTest {

    @Autowired
    private InsightService userInsightsService;

    static LoadingCache<Integer, Set<UserInsight>> insightsCache = Mockito.mock(LoadingCache.class);

    @Override
    protected void before() {
        try {
            MockitoAnnotations.initMocks(this);
            when(insightsCache.get(anyInt())).thenReturn(new HashSet<>(getTestUserInsights()));
            when(insightsCache.getAllPresent(anySet())).thenReturn(Collections.singletonMap(1, new HashSet<>(getTestUserInsights())));
            truncateTables(TABLE);
            prepareTestData(getInsertData());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findAll_byUsername() {
        final PagedResult<UserInsightDTO> pagedResult = userInsightsService.findAll("admin@exrates.me");
        assertEquals(getTestPageResult().getItems().get(0).getEmail(), pagedResult.getItems().get(0).getEmail());
        assertFalse(pagedResult.isHasNextPage());
    }

    @Test
    public void findAll_byUserId() {
        final PagedResult<UserInsightDTO> pagedResult = userInsightsService.findAll(1);
        assertEquals(getTestPageResult().getItems().get(0).getEmail(), pagedResult.getItems().get(0).getEmail());
        assertFalse(pagedResult.isHasNextPage());
    }

    @Test
    public void findAll() {
        final PagedResult<UserInsightDTO> pagedResult = userInsightsService.findAll(10, 0);
        assertEquals(2, pagedResult.getItems().size());
        assertEquals(getTestPageResult().getItems().get(0).getEmail(), pagedResult.getItems().get(0).getEmail());
        assertEquals("admin1@exrates.me", pagedResult.getItems().get(1).getEmail());
        assertFalse(pagedResult.isHasNextPage());
    }

    private PagedResult<UserInsightDTO> getTestPageResult() {
        PagedResult<UserInsightDTO> page = new PagedResult<>();
        page.setHasNextPage(false);
        page.setItems(ImmutableList.of(getTestUserInsightDTO()));
        return page;
    }

    private UserInsightDTO getTestUserInsightDTO() {
        UserInsightDTO dto = new UserInsightDTO();
        dto.setUserId(1);
        dto.setEmail("admin@exrates.me");
        dto.setDeposit(BigDecimal.valueOf(2));
        dto.setWithdrawal(BigDecimal.valueOf(2));
        dto.setDepositGt10k(false);
        dto.setWithdrawGt10k(false);
        dto.setTradeComDay(BigDecimal.ZERO);
        dto.setTransferComDay(BigDecimal.ZERO);
        dto.setInoutComDay(BigDecimal.ZERO);
        dto.setTradeComWeek(BigDecimal.ZERO);
        dto.setTransferComWeek(BigDecimal.ZERO);
        dto.setInoutComWeek(BigDecimal.ZERO);
        dto.setTradeComMonth(BigDecimal.ZERO);
        dto.setTransferComMonth(BigDecimal.ZERO);
        dto.setInoutComMonth(BigDecimal.ZERO);
        dto.setTradeComYear(BigDecimal.valueOf(2));
        dto.setTransferComYear(BigDecimal.valueOf(0.4));
        dto.setInoutComYear(BigDecimal.valueOf(0.4));
        dto.setChangeBalanceDay(BigDecimal.ZERO);
        dto.setChangeBalanceWeek(BigDecimal.ZERO);
        dto.setChangeBalanceMonth(BigDecimal.ZERO);
        dto.setChangeBalanceYear(BigDecimal.valueOf(-20));
        dto.setTradeNumberDay(0);
        dto.setTradeAmountDay(BigDecimal.ZERO);
        dto.setTradeNumberWithRefillDay("0 / false");
        dto.setTradeNumberWithWithdrawDay("0 / false");
        dto.setTradeNumberWeek(0);
        dto.setTradeAmountWeek(BigDecimal.ZERO);
        dto.setTradeNumberWithRefillWeek("0 / false");
        dto.setTradeNumberWithWithdrawWeek("0 / false");
        dto.setTradeNumberMonth(0);
        dto.setTradeAmountMonth(BigDecimal.ZERO);
        dto.setTradeNumberWithRefillMonth("0 / false");
        dto.setTradeNumberWithWithdrawMonth("0 / false");
        dto.setTradeNumberYear(16);
        dto.setTradeAmountYear(BigDecimal.valueOf(40));
        dto.setTradeNumberWithRefillYear("16 / false");
        dto.setTradeNumberWithWithdrawYear("16 / true");
        dto.setNoDealsButRefilledDay(false);
        dto.setNoDealsButWithdrawAndRefilledDay(false);
        dto.setNoDealsButRefilledWeek(false);
        dto.setNoDealsButWithdrawAndRefilledWeek(false);
        dto.setNoDealsButRefilledMonth(false);
        dto.setNoDealsButWithdrawAndRefilledMonth(false);
        dto.setNoDealsButRefilledYear(false);
        dto.setNoDealsButWithdrawAndRefilledYear(false);
        return dto;
    }

    @Configuration
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_CORE_NP_TEMPLATE)
        private NamedParameterJdbcOperations coreParameterJdbcOperations;

        @Autowired
        @Qualifier(TEST_CORE_TEMPLATE)
        private JdbcOperations coreJdbcOperations;

        @Autowired
        @Qualifier(TEST_ADMIN_NP_TEMPLATE)
        private NamedParameterJdbcOperations adminParameterJdbcOperations;

        @Override
        protected String getSchema() {
            return "UserInsightsServiceImplTest";
        }

        @Bean
        CoreUserRepository coreUserRepository() {
            return new CoreUserRepositoryImpl(coreParameterJdbcOperations, coreJdbcOperations);
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
        InsightService userInsightsService() {
            return new UserInsightServiceImpl(coreUserRepository(), userInsightRepository(), userInoutStatusRepository(), insightsCache);
        }
    }
}
