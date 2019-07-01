package me.exrates.adminservice.repository.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import config.DataComparisonTest;
import me.exrates.adminservice.domain.UserInsight;
import me.exrates.adminservice.repository.UserInsightRepository;
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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static me.exrates.adminservice.repository.UserInsightRepository.TABLE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        UserInsightRepositoryImplTest.InnerConfig.class
})
public class UserInsightRepositoryImplTest extends DataComparisonTest {

    public static final String SELECT_ALL_SQL = "SELECT * FROM " + TABLE;
    public static final String INSERT_SQL = "INSERT INTO " + TABLE + " (created, user_id, rate_btc_for_one_usd, " +
            "refill_amount_usd, withdraw_amount_usd, inout_commission_usd, transfer_in_amount_usd, transfer_out_amount_usd, " +
            "transfer_commission_usd, trade_sell_count, trade_buy_count, trade_amount_usd, trade_commission_usd, " +
            "balance_dynamics_usd, source_ids)";

    @Autowired
    private UserInsightRepository userInsightRepository;

    @Override
    protected void before() {
        setDatabaseType(ADMIN_DATABASE_TYPE);
        try {
            truncateTables(TABLE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void findAll_whenOk() throws SQLException {
        prepareTestData(getInsertData());

        final List<UserInsight> userInsights = userInsightRepository.findAll(0, 0);
        assertEquals(4, userInsights.size());
    }

    @Test
    public void findAll_whenEmpty() {
        final List<UserInsight> userInsights = userInsightRepository.findAll(0, 0);
        assertEquals(0, userInsights.size());
    }

    @Test
    public void findAllByUserId_whenOk() throws SQLException {
        prepareTestData(getInsertData());
        final List<UserInsight> userInsights = new ArrayList<>();

        around()
                .withSQL(SELECT_ALL_SQL)
                .withObject(getTestUserInsights())
                .run(() -> userInsights.addAll(userInsightRepository.findAllByUserId(1)));

        assertEquals(2, userInsights.size());
    }

    @Test
    public void getActiveUserIds() throws SQLException {
        prepareTestData(getInsertData());
        final Set<Integer> activeUserIds = userInsightRepository.getActiveUserIds(10, 0);
        assertThat(activeUserIds, is(ImmutableSet.of(1, 2)));
    }

    @Test
    public void findAllByUserId_whenEmpty() {
        final Set<UserInsight> userInsights = userInsightRepository.findAllByUserId(10);
        assertEquals(0, userInsights.size());
    }

    public static List<UserInsight> getTestUserInsights() {
        UserInsight insight1 = UserInsight.builder()
                .created(LocalDate.of(2019, 3, 3))
                .userId(1)
                .rateBtcForOneUsd(new BigDecimal(0.0000015))
                .refillAmountUsd(BigDecimal.ONE)
                .withdrawAmountUsd(BigDecimal.ONE)
                .inoutCommissionUsd(new BigDecimal(0.2))
                .transferInAmountUsd(BigDecimal.ONE)
                .transferOutAmountUsd(BigDecimal.ONE)
                .transferCommissionUsd(new BigDecimal(0.2))
                .tradeSellCount(4)
                .tradeBuyCount(4)
                .tradeAmountUsd(new BigDecimal(20))
                .tradeCommissionUsd(BigDecimal.ONE)
                .balanceDynamicsUsd(new BigDecimal(-10))
                .sourceIds(ImmutableList.of(1, 2, 3, 4))
                .build();
        UserInsight insight2 = UserInsight.builder()
                .created(LocalDate.of(2019, 3, 4))
                .userId(1)
                .rateBtcForOneUsd(new BigDecimal(0.0000015))
                .refillAmountUsd(BigDecimal.ONE)
                .withdrawAmountUsd(BigDecimal.ONE)
                .inoutCommissionUsd(new BigDecimal(0.2))
                .transferInAmountUsd(BigDecimal.ONE)
                .transferOutAmountUsd(BigDecimal.ONE)
                .transferCommissionUsd(new BigDecimal(0.2))
                .tradeSellCount(4)
                .tradeBuyCount(4)
                .tradeAmountUsd(new BigDecimal(20))
                .tradeCommissionUsd(BigDecimal.ONE)
                .balanceDynamicsUsd(new BigDecimal(-10))
                .sourceIds(ImmutableList.of(1, 2, 3, 4))
                .build();

        return ImmutableList.of(insight1, insight2);
    }

    public static String getInsertData() {
        return INSERT_SQL + " VALUES"
                + " (\'2019-03-03\', 1, 0.0000015, 1, 1, 0.2, 1, 1, 0.2, 4, 4, 20, 1, -10, \'1, 2, 3, 4\'),"
                + " (\'2019-03-04\', 1, 0.0000015, 1, 1, 0.2, 1, 1, 0.2, 4, 4, 20, 1, -10, \'1, 2, 3, 4\'),"
                + " (\'2019-03-03\', 2, 0.0000015, 1, 1, 0.2, 1, 1, 0.2, 4, 4, 20, 1, -10, \'1, 2, 3, 4\'),"
                + " (\'2019-03-04\', 2, 0.0000015, 1, 1, 0.2, 1, 1, 0.2, 4, 4, 20, 1, -10, \'1, 2, 3, 4\');";
    }

    @Configuration
    @Profile("test")
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier(TEST_ADMIN_NP_TEMPLATE)
        private NamedParameterJdbcOperations namedParameterJdbcOperations;

        @Override
        protected String getSchema() {
            return "UserInsightRepositoryImplTest";
        }

        @Bean
        UserInsightRepository userInsightRepository() {
            return new UserInsightRepositoryImpl(namedParameterJdbcOperations);
        }
    }
}